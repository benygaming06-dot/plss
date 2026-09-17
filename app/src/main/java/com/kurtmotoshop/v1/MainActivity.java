package com.kurtmotoshop.v1;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Locale;

public class MainActivity extends android.app.Activity {
    private LinearLayout root, content;
    private TextView productCount, serviceCount;
    private SharedPreferences prefs;

    private final int BG = Color.rgb(5, 6, 8);
    private final int CARD = Color.rgb(14, 17, 21);
    private final int RED = Color.rgb(235, 25, 35);
    private final int BLUE = Color.rgb(20, 120, 235);
    private final int GREEN = Color.rgb(30, 210, 95);
    private final int ORANGE = Color.rgb(255, 145, 25);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(165, 169, 176);

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("inventory", MODE_PRIVATE);
        buildUI();
    }

    private TextView tv(String text, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private GradientDrawable bg(int color, float radius, int stroke, int strokeColor) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        if (stroke > 0) g.setStroke(stroke, strokeColor);
        return g;
    }

    private LinearLayout row() {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        return r;
    }

    private LinearLayout box(int color, int strokeColor) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(18, 16, 18, 16);
        l.setBackground(bg(color, 28, 2, strokeColor));
        return l;
    }

    private TextView button(String text, int color) {
        TextView b = tv(text, 14, WHITE, true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(10, 13, 10, 13);
        b.setBackground(bg(color, 20, 0, color));
        return b;
    }

    private void buildUI() {
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 10, 16, 24);
        root.setBackgroundColor(BG);
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout header = row();
        header.setPadding(4, 8, 4, 8);

        TextView logo = tv("K", 32, RED, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(bg(Color.rgb(18,20,24), 18, 2, RED));
        header.addView(logo, new LinearLayout.LayoutParams(58,58));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(12,0,0,0);
        brand.addView(tv("KURT DHYLAN", 21, WHITE, true));
        brand.addView(tv("MOTO SHOP INVENTORY", 10, MUTED, true));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView settings = tv("⚙", 26, WHITE, false);
        settings.setGravity(Gravity.CENTER);
        settings.setOnClickListener(v -> showSettings());
        header.addView(settings, new LinearLayout.LayoutParams(55,55));
        root.addView(header);

        TextView dash = tv("DASHBOARD", 28, WHITE, true);
        dash.setPadding(4, 22, 4, 2);
        root.addView(dash);

        TextView offline = tv("●  OFFLINE MODE     Products & services are saved on this phone", 12, GREEN, true);
        offline.setPadding(14, 14, 14, 14);
        offline.setBackground(bg(Color.rgb(13,27,19), 20, 1, GREEN));
        root.addView(offline);

        LinearLayout stats = row();
        stats.setPadding(0, 14, 0, 0);
        LinearLayout pStat = statCard("📦", "PRODUCTS / PARTS", RED, true);
        LinearLayout sStat = statCard("🔧", "SERVICES / LABOR", BLUE, false);
        stats.addView(pStat, new LinearLayout.LayoutParams(0,112,1));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0,112,1);
        sp.setMargins(10,0,0,0);
        stats.addView(sStat, sp);
        root.addView(stats);

        TextView title = tv("QUICK ACTIONS", 18, WHITE, true);
        title.setPadding(4,24,4,10);
        root.addView(title);

        LinearLayout grid1 = row();
        LinearLayout prod = actionCard("▣", "PRODUCTS / PARTS", "View, search and manage stock", RED);
        LinearLayout serv = actionCard("⚒", "SERVICES / LABOR", "View, search and manage services", BLUE);
        grid1.addView(prod, new LinearLayout.LayoutParams(0,160,1));
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(0,160,1);
        mp.setMargins(10,0,0,0);
        grid1.addView(serv, mp);
        root.addView(grid1);

        LinearLayout grid2 = row();
        LinearLayout addP = actionCard("+", "ADD PRODUCT", "Name • Brand • Model • Price • Stock", GREEN);
        LinearLayout addS = actionCard("+", "ADD SERVICE", "Labor name • Price", ORANGE);
        grid2.addView(addP, new LinearLayout.LayoutParams(0,160,1));
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(0,160,1);
        ap.setMargins(10,0,0,0);
        grid2.addView(addS, ap);
        root.addView(grid2);

        TextView toolsTitle = tv("TOOLS", 18, WHITE, true);
        toolsTitle.setPadding(4,24,4,10);
        root.addView(toolsTitle);

        LinearLayout tools = row();
        String[] labels = {"Products", "Services", "Cart", "Settings"};
        String[] icons = {"▣", "⚒", "🛒", "⚙"};
        int[] colors = {RED, BLUE, ORANGE, GREEN};
        for (int i=0;i<labels.length;i++) {
            final int idx=i;
            LinearLayout c = box(CARD, Color.rgb(35,39,45));
            c.setGravity(Gravity.CENTER);
            TextView ic = tv(icons[i], 23, colors[i], true); ic.setGravity(Gravity.CENTER);
            TextView lb = tv(labels[i], 11, WHITE, false); lb.setGravity(Gravity.CENTER);
            c.addView(ic, new LinearLayout.LayoutParams(-1,42));
            c.addView(lb, new LinearLayout.LayoutParams(-1,30));
            if (idx==0) c.setOnClickListener(v -> showProducts());
            if (idx==1) c.setOnClickListener(v -> showServices());
            if (idx==2) c.setOnClickListener(v -> showCart());
            if (idx==3) c.setOnClickListener(v -> showSettings());
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0,94,1);
            if(i>0) tp.setMargins(8,0,0,0);
            tools.addView(c,tp);
        }
        root.addView(tools);

        TextView footer = tv("KURT DHYLAN MOTO SHOP\nInventory • Services • Sales\n\nBUILT FOR BIKERS", 11, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(10,24,10,10);
        root.addView(footer);

        refreshCounts();
        prod.setOnClickListener(v -> showProducts());
        serv.setOnClickListener(v -> showServices());
        addP.setOnClickListener(v -> addProduct());
        addS.setOnClickListener(v -> addService());
    }

    private LinearLayout statCard(String icon, String title, int accent, boolean product) {
        LinearLayout c = box(CARD, accent);
        LinearLayout r = row();
        TextView ic = tv(icon, 25, WHITE, false);
        ic.setGravity(Gravity.CENTER);
        r.addView(ic, new LinearLayout.LayoutParams(48,48));
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setPadding(10,0,0,0);
        texts.addView(tv(title,10,MUTED,true));
        TextView n=tv("0",27,WHITE,true);
        if(product) productCount=n; else serviceCount=n;
        texts.addView(n);
        r.addView(texts, new LinearLayout.LayoutParams(0,-2,1));
        c.addView(r);
        return c;
    }

    private LinearLayout actionCard(String icon, String title, String desc, int accent) {
        LinearLayout c = box(Color.rgb(12,15,18), accent);
        TextView i = tv(icon, 38, accent, true); i.setGravity(Gravity.CENTER);
        TextView t = tv(title, 15, WHITE, true); t.setPadding(0,8,0,2);
        TextView d = tv(desc, 11, MUTED, false); d.setMaxLines(3);
        c.addView(i, new LinearLayout.LayoutParams(-1,50));
        c.addView(t); c.addView(d);
        return c;
    }

    private JSONArray getArray(String key) {
        try { return new JSONArray(prefs.getString(key, "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }

    private void saveArray(String key, JSONArray a) {
        prefs.edit().putString(key, a.toString()).apply();
        refreshCounts();
    }

    private void refreshCounts() {
        if (productCount != null) productCount.setText(String.valueOf(getArray("products").length()));
        if (serviceCount != null) serviceCount.setText(String.valueOf(getArray("services").length()));
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextColor(WHITE);
        e.setHintTextColor(MUTED);
        e.setSingleLine(true);
        e.setPadding(14, 10, 14, 10);
        e.setBackground(bg(Color.rgb(25,28,33), 14, 1, Color.rgb(55,60,68)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 52);
        lp.setMargins(0,5,0,5);
        e.setLayoutParams(lp);
        return e;
    }

    private void addProduct() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(4,0,4,0);
        EditText name=field("Product / Part Name");
        EditText brand=field("Brand");
        EditText model=field("Model");
        EditText price=field("Price (₱)");
        EditText stock=field("Stocks");
        price.setInputType(2|8192);
        stock.setInputType(2);
        form.addView(name); form.addView(brand); form.addView(model); form.addView(price); form.addView(stock);

        AlertDialog d = new AlertDialog.Builder(this).setTitle("ADD PRODUCT / PART")
                .setView(form).setNegativeButton("CANCEL",null)
                .setPositiveButton("SAVE",null).create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if(name.getText().toString().trim().isEmpty()) { name.setError("Required"); return; }
            try {
                JSONObject o=new JSONObject();
                o.put("name",name.getText().toString().trim());
                o.put("brand",brand.getText().toString().trim());
                o.put("model",model.getText().toString().trim());
                o.put("price",price.getText().toString().trim());
                o.put("stock",Integer.parseInt(stock.getText().toString().trim().isEmpty()?"0":stock.getText().toString().trim()));
                JSONArray a=getArray("products"); a.put(o); saveArray("products",a);
                Toast.makeText(this,"Product saved",Toast.LENGTH_SHORT).show(); d.dismiss();
            } catch(Exception e) { stock.setError("Enter a valid stock number"); }
        }));
        d.show();
    }

    private void addService() {
        LinearLayout form=new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL);
        EditText name=field("Labor / Service Name");
        EditText price=field("Price (₱)"); price.setInputType(2|8192);
        form.addView(name); form.addView(price);
        AlertDialog d=new AlertDialog.Builder(this).setTitle("ADD SERVICE / LABOR")
                .setView(form).setNegativeButton("CANCEL",null).setPositiveButton("SAVE",null).create();
        d.setOnShowListener(x -> d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if(name.getText().toString().trim().isEmpty()){name.setError("Required");return;}
            try {
                JSONObject o=new JSONObject(); o.put("name",name.getText().toString().trim()); o.put("price",price.getText().toString().trim());
                JSONArray a=getArray("services"); a.put(o); saveArray("services",a);
                Toast.makeText(this,"Service saved",Toast.LENGTH_SHORT).show(); d.dismiss();
            } catch(Exception e) { price.setError("Invalid price"); }
        }));
        d.show();
    }

    private void showProducts() { showList(true); }
    private void showServices() { showList(false); }

    private void showList(boolean products) {
        final String key=products?"products":"services";
        final JSONArray all=getArray(key);
        LinearLayout outer=new LinearLayout(this); outer.setOrientation(LinearLayout.VERTICAL); outer.setPadding(12,4,12,4);
        EditText search=field(products?"Search products...":"Search services...");
        outer.addView(search);
        LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv=new ScrollView(this); sv.addView(list); outer.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(products?"PRODUCTS / PARTS":"SERVICES / LABOR")
                .setView(outer).setPositiveButton("CLOSE",null).create();

        Runnable render=() -> {
            list.removeAllViews();
            String q=search.getText().toString().toLowerCase(Locale.US);
            int shown=0;
            for(int i=0;i<all.length();i++) {
                try {
                    JSONObject o=all.getJSONObject(i);
                    String text=products
                            ? o.optString("name")+" "+o.optString("brand")+" "+o.optString("model")
                            : o.optString("name");
                    if(!text.toLowerCase(Locale.US).contains(q)) continue;
                    shown++;
                    list.addView(itemCard(o,i,products,dialog));
                } catch(Exception ignored){}
            }
            if(shown==0) list.addView(tv("No items found.",15,MUTED,false));
        };
        search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){} public void onTextChanged(CharSequence s,int a,int b,int c){render.run();} public void afterTextChanged(Editable e){}});
        render.run();
        dialog.show();
    }

    private View itemCard(JSONObject o,int index,boolean product,AlertDialog dialog) {
        LinearLayout c=box(CARD,product?RED:BLUE);
        TextView title=tv(o.optString("name"),17,WHITE,true); c.addView(title);
        if(product) {
            c.addView(tv(o.optString("brand")+"  •  "+o.optString("model"),12,MUTED,false));
            c.addView(tv("₱"+o.optString("price","0")+"    |    STOCK: "+o.optInt("stock",0),13,WHITE,true));
        } else {
            c.addView(tv("LABOR PRICE: ₱"+o.optString("price","0"),13,WHITE,true));
        }
        LinearLayout actions=row(); actions.setPadding(0,8,0,0);
        TextView edit=button("EDIT",BLUE), del=button("DELETE",RED);
        actions.addView(edit,new LinearLayout.LayoutParams(0,45,1));
        LinearLayout.LayoutParams dp=new LinearLayout.LayoutParams(0,45,1); dp.setMargins(8,0,0,0); actions.addView(del,dp);
        c.addView(actions);
        edit.setOnClickListener(v -> editItem(index,product,dialog));
        del.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("DELETE ITEM?").setMessage(o.optString("name"))
                    .setNegativeButton("CANCEL",null).setPositiveButton("DELETE",(x,w)->{
                        JSONArray a=getArray(product?"products":"services");
                        if(index<a.length()){a.remove(index);saveArray(product?"products":"services",a);dialog.dismiss();showList(product);}
                    }).show();
        });
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,5,0,5); c.setLayoutParams(lp);
        return c;
    }

    private void editItem(int index,boolean product,AlertDialog oldDialog) {
        JSONArray a=getArray(product?"products":"services");
        if(index>=a.length()) return;
        try {
            JSONObject o=a.getJSONObject(index);
            LinearLayout form=new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL);
            EditText name=field(product?"Product / Part Name":"Labor / Service Name"); name.setText(o.optString("name"));
            EditText brand=null,model=null,price=field("Price (₱)");
            price.setText(o.optString("price"));
            EditText stock=null;
            if(product){brand=field("Brand");brand.setText(o.optString("brand"));model=field("Model");model.setText(o.optString("model"));stock=field("Stocks");stock.setText(String.valueOf(o.optInt("stock",0)));stock.setInputType(2);}
            price.setInputType(2|8192);
            form.addView(name); if(product){form.addView(brand);form.addView(model);} form.addView(price); if(product)form.addView(stock);
            AlertDialog d=new AlertDialog.Builder(this).setTitle("EDIT "+(product?"PRODUCT":"SERVICE")).setView(form)
                    .setNegativeButton("CANCEL",null).setPositiveButton("SAVE",null).create();
            EditText finalBrand=brand, finalModel=model, finalStock=stock;
            d.setOnShowListener(x->d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
                try{
                    o.put("name",name.getText().toString().trim()); o.put("price",price.getText().toString().trim());
                    if(product){o.put("brand",finalBrand.getText().toString().trim());o.put("model",finalModel.getText().toString().trim());o.put("stock",Integer.parseInt(finalStock.getText().toString().trim()));}
                    a.put(index,o);saveArray(product?"products":"services",a);d.dismiss();oldDialog.dismiss();showList(product);
                }catch(Exception e){if(finalStock!=null)finalStock.setError("Invalid stock");}
            }));
            d.show();
        }catch(Exception ignored){}
    }

    private void showCart() {
        final JSONArray cart=getArray("cart");
        StringBuilder msg=new StringBuilder();
        double total=0;
        for(int i=0;i<cart.length();i++){
            JSONObject o=cart.optJSONObject(i); if(o==null)continue;
            double p=o.optDouble("price",0); int q=o.optInt("qty",1); total+=p*q;
            msg.append(o.optString("name")).append(" x").append(q).append(" = ₱").append(String.format(Locale.US,"%.2f",p*q)).append("\n");
        }
        if(cart.length()==0) msg.append("Cart is empty.\n\nYou can use this section for future sales checkout.");
        msg.append("\nTOTAL: ₱").append(String.format(Locale.US,"%.2f",total));
        new AlertDialog.Builder(this).setTitle("CART / SALES").setMessage(msg.toString())
                .setNegativeButton("CLEAR CART",(d,w)->prefs.edit().remove("cart").apply()).setPositiveButton("CLOSE",null).show();
    }

    private void showSettings() {
        new AlertDialog.Builder(this).setTitle("KURT DHYLAN MOTO SHOP")
                .setMessage("Version 2.0\n\nOffline database: ON\nProducts: "+getArray("products").length()+"\nServices: "+getArray("services").length()+"\n\nYour inventory is stored locally on this phone.")
                .setPositiveButton("OK",null).show();
    }
}
