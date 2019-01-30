package faithworks.diabetesmonitoring.android.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import faithworks.diabetesmonitoring.android.Constants;
import faithworks.diabetesmonitoring.android.R;
import faithworks.diabetesmonitoring.android.db.DatabaseHandler;
import faithworks.diabetesmonitoring.android.object.A1cEstimate;
import faithworks.diabetesmonitoring.android.tools.GlucosioConverter;
import faithworks.diabetesmonitoring.android.tools.NumberFormatUtils;
import faithworks.diabetesmonitoring.android.tools.ReadingTools;

public class A1cEstimateAdapter extends ArrayAdapter<A1cEstimate> {
    private final DatabaseHandler databaseHandler;
    private final NumberFormat numberFormat = NumberFormatUtils.createDefaultNumberFormat();

    public A1cEstimateAdapter(Context context, int resource, List<A1cEstimate> items) {
        super(context, resource, items);
        databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.dialog_a1c_item, parent, false);
        }

        A1cEstimate p = getItem(position);

        if (p != null) {
            TextView value = v.findViewById(R.id.dialog_a1c_item_value);
            TextView month = v.findViewById(R.id.dialog_a1c_item_month);
            TextView glucoseAverage = v.findViewById(R.id.dialog_a1c_item_glucose_value);

            if (value != null) {
                if ("percentage".equals(databaseHandler.getUser(1).getPreferred_unit_a1c())) {
                    String stringValue = p.getValue() + " %";
                    value.setText(stringValue);
                } else {
                    String stringValue = GlucosioConverter.a1cNgspToIfcc(p.getValue()) + " mmol/mol";
                    value.setText(stringValue);
                }
            }

            if (month != null) {
                month.setText(p.getMonth());
            }

            if (glucoseAverage != null) {
                if (Constants.Units.MG_DL.equals(databaseHandler.getUser(1).getPreferred_unit())) {
                    glucoseAverage.setText(getContext().getString(R.string.mg_dL_value, p.getGlucoseAverage()));
                } else {
                    double mmol = GlucosioConverter.glucoseToMgDl(ReadingTools.safeParseDouble(p.getGlucoseAverage()));
                    String reading = numberFormat.format(mmol);
                    glucoseAverage.setText(getContext().getString(R.string.mmol_L_value, reading));
                }
            }
        }

        return v;
    }
}
