package com.example.zgbeautyandhair;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zgbeautyandhair.Adapter.MyCartAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Database.CartDataSource;
import com.example.zgbeautyandhair.Database.CartDatabase;
import com.example.zgbeautyandhair.Database.LocalCartDataSource;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    MyCartAdapter adapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    CartDataSource cartDataSource;

    RelativeLayout cart_activity;

    @BindView(R.id.back_pressed)
    ImageView back_pressed;

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;

    @BindView(R.id.txt_total_price)
    TextView txt_total_price;

    @BindView(R.id.btn_payment)
    Button btn_payment;

    @BindView(R.id.btn_clear_cart)
    ImageView btn_clear_cart;

    @OnClick(R.id.btn_clear_cart)
    void Clear() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("מחיקת כל עגלת הקניות")
                .setMessage("האם את מעוניינת להסיר הכל? ")
                .setNegativeButton("ביטול", (dialog, which) -> {
                    dialog.dismiss();
                }).setPositiveButton("הסרה", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update Adapter
                        cartDataSource.clearCart(Common.currentUser.getPhoneNumber())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@NonNull Integer integer) {
                                        //we need load all cart again after we clear
                                        Toast mToast = new Toast(CartActivity.this);
                                        mToast.setDuration(Toast.LENGTH_LONG);
                                        View toastView = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                        TextView textView = toastView.findViewById(R.id.txt_message);
                                        mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                        textView.setText("המוצרים נמחקו");
                                        mToast.setView(toastView);
                                        mToast.show();

                                        compositeDisposable.add(
                                                cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe( cartItems -> {
                                                            //after done we just sum
                                                            // after delete all items, we need update total price
                                                            cartDataSource.sumPrice(Common.currentUser.getPhoneNumber())
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(updatePrice());

                                                        }, throwable -> {
                                                            Toast.makeText( CartActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT ).show();
                                                        })
                                        );
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Toast.makeText( CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                                    }
                                });

                        getAllCart();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(CartActivity.this);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        cart_activity = findViewById(R.id.cart_activity);

        getAllCart();

        txt_total_price = findViewById(R.id.txt_total_price);

        btn_payment = findViewById(R.id.btn_payment);

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaymentConfirm();
            }
        });

        //View
        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(linearLayoutManager);

    }

    private void PaymentConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("תשלום");
        builder.setIcon(R.drawable.ic_credit_card);


        View view = LayoutInflater.from(this).inflate(R.layout.layout_confirm_order, null);

        EditText edt_address = view.findViewById(R.id.edt_address);
        EditText edt_comment = view.findViewById(R.id.edt_comment);
        RadioButton rdi_home = view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_braintree = view.findViewById(R.id.rdi_braintree);

        //By default we select home address, user address
        edt_address.setText(Common.currentUser.getAddress());

        //Event
        rdi_home.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edt_address.setText(Common.currentUser.getAddress());
            }
        });

        rdi_other_address.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edt_address.setText("");
                edt_address.setHint("");
               // edt_address.setVisibility(View.VISIBLE);
            }
        });


        builder.setView(view);
        builder.setNegativeButton("ביטול", (dialog, which) ->
                dialog.dismiss())
                .setPositiveButton("המשך", (dialog, which) -> {

                    if (rdi_braintree.isChecked()) {

                        String price = txt_total_price.getText().toString();
                        String comment = edt_comment.getText().toString();
                        String other_address = edt_address.getText().toString();

                        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                        intent.putExtra("price", price);
                        intent.putExtra("comment", comment);
                        intent.putExtra("other_address", other_address);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getAllCart() {
        compositeDisposable.add(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( cartItems -> {

                    adapter = new MyCartAdapter(this, cartItems);
                    recycler_cart.setAdapter(adapter);

                    cartDataSource.sumPrice(Common.currentUser.getPhoneNumber())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(updatePrice());

                }, throwable -> {
                    Toast.makeText( this, ""+throwable.getMessage(), Toast.LENGTH_SHORT ).show();
                }));
    }

    private SingleObserver<? super Long> updatePrice() {
        return new SingleObserver<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(Long aLong) {
                //move method from onSumCartSuccess
                txt_total_price.setText(new StringBuilder("₪").append(aLong));
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage().contains("Query returned empty"))
                    txt_total_price.setText("");
                else
                    Toast.makeText( CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

                finish();
            }
        };
    }

    public void back(View view) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }

    @Override
    protected void onDestroy() {
        if (adapter != null)
            adapter.onDestroy();
        compositeDisposable.clear();
        super.onDestroy();
    }
}