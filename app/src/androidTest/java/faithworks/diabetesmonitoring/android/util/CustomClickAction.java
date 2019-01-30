/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package faithworks.diabetesmonitoring.android.util;

/**
 * @author amouly on 11/12/15.
 */

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.PrecisionDescriber;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.Tapper;
import android.support.test.espresso.core.internal.deps.guava.base.Optional;
import android.support.test.espresso.util.HumanReadables;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static org.hamcrest.Matchers.allOf;

/**
 * Enables clicking on views.
 */
public final class CustomClickAction implements ViewAction {
    private final CoordinatesProvider coordinatesProvider;
    private final Tapper tapper;
    private final PrecisionDescriber precisionDescriber;
    private final Optional<ViewAction> rollbackAction;

    public CustomClickAction(Tapper tapper, CoordinatesProvider coordinatesProvider,
                             PrecisionDescriber precisionDescriber) {
        this(tapper, coordinatesProvider, precisionDescriber, null);
    }

    public CustomClickAction(Tapper tapper, CoordinatesProvider coordinatesProvider,
                             PrecisionDescriber precisionDescriber, ViewAction rollbackAction) {
        this.coordinatesProvider = coordinatesProvider;
        this.tapper = tapper;
        this.precisionDescriber = precisionDescriber;
        this.rollbackAction = Optional.fromNullable(rollbackAction);
    }

    public static ViewAction click() {
        return new CustomClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Matcher<View> getConstraints() {
        Matcher<View> standardConstraint = isDisplayingAtLeast(10);
        if (rollbackAction.isPresent()) {
            return allOf(standardConstraint, rollbackAction.get().getConstraints());
        } else {
            return standardConstraint;
        }
    }

    @Override
    public void perform(UiController uiController, View view) {
        float[] coordinates = coordinatesProvider.calculateCoordinates(view);
        float[] precision = precisionDescriber.describePrecision();
        Tapper.Status status = Tapper.Status.FAILURE;
        int loopCount = 0;
        // Native event injection is quite a tricky process. A tap is actually 2
        // seperate motion events which need to get injected into the system. Injection
        // makes an RPC call from our app under test to the Android system server, the
        // system server decides which window layer to deliver the event to, the system
        // server makes an RPC to that window layer, that window layer delivers the event
        // to the correct UI element, activity, or window object. Now we need to repeat
        // that 2x. for a simple down and up. Oh and the down event triggers timers to
        // detect whether or not the event is a long vs. short press. The timers are
        // removed the moment the up event is received (NOTE: the possibility of eventTime
        // being in the future is totally ignored by most motion event processors).
        //
        // Phew.
        //
        // The net result of this is sometimes we'll want to do a regular tap, and for
        // whatever reason the up event (last half) of the tap is delivered after long
        // press timeout (depending on system load) and the long press behaviour is
        // displayed (EG: show a context menu). There is no way to avoid or handle this more
        // gracefully. Also the longpress behavour is app/widget specific. So if you have
        // a seperate long press behaviour from your short press, you can pass in a
        // 'RollBack' ViewAction which when executed will undo the effects of long press.
        while (status != Tapper.Status.SUCCESS && loopCount < 3) {
            try {
                status = tapper.sendTap(uiController, coordinates, precision);
            } catch (RuntimeException re) {
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(re)
                        .build();
            }
            int duration = ViewConfiguration.getPressedStateDuration();
            // ensures that all work enqueued to process the tap has been run.
            if (duration > 0) {
                uiController.loopMainThreadForAtLeast(duration);
            }
            if (status == Tapper.Status.WARNING) {
                if (rollbackAction.isPresent()) {
                    rollbackAction.get().perform(uiController, view);
                } else {
                    break;
                }
            }
            loopCount++;
        }
        if (status == Tapper.Status.FAILURE) {
            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new RuntimeException(String.format("Couldn't "
                                    + "click at: %s,%s precision: %s, %s . Tapper: %s coordinate provider: %s precision " +
                                    "describer: %s. Tried %s times. With Rollback? %s", coordinates[0], coordinates[1],
                            precision[0], precision[1], tapper, coordinatesProvider, precisionDescriber, loopCount,
                            rollbackAction.isPresent())))
                    .build();
        }
        if (tapper == Tap.SINGLE && view instanceof WebView) {
            // WebViews will not process click events until double tap
            // timeout. Not the best place for this - but good for now.
            uiController.loopMainThreadForAtLeast(ViewConfiguration.getDoubleTapTimeout());
        }
    }

    @Override
    public String getDescription() {
        return tapper.toString().toLowerCase() + " click";
    }
}
