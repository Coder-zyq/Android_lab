package cn.chonor.lab3;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton=null;
    private Data data=new Data();
    private RecyclerView mRecyclerView = null;
    private CommonAdapter commonAdapter;
    private ScaleInAnimationAdapter animationAdapter;
    private Button button;
    private Boolean flag;

    protected RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        init_listener();
    }
    //数据回传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent); //数据回传
        if (resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            ArrayList<Good> tmp= extras.getParcelableArrayList("data");
            ArrayList<Good> tmp1= extras.getParcelableArrayList("cart");
            data.getGood_list().clear();
            animationAdapter.notifyDataSetChanged();
            mRecyclerView.removeAllViews();
            data.setGood_list(tmp);
            data.setCart_list(tmp1);
            change();
        }
    }

    private void init() {
        flag=true;
        button = (Button) findViewById(R.id.button);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton); //初始化按钮
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView); //初始化RecyclerView

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        commonAdapter = new CommonAdapter(MainActivity.this, R.layout.items, data.getGood_list(), flag);
        animationAdapter = new ScaleInAnimationAdapter(commonAdapter);

        animationAdapter.setDuration(1000); //设置开始动画
        mRecyclerView.setAdapter(animationAdapter);//填充数据
        mRecyclerView.setItemAnimator(new OvershootInLeftAnimator());
        mRecyclerView.getItemAnimator().setRemoveDuration(300); //设置移除延时

    }
    private void init_listener(){
        Adapter_listener();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override //跳转至 购物车界面
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Shoppingcart.class);
                Bundle bundle = new Bundle();//传输数据
                bundle.putParcelableArrayList("data",data.getGood_list());
                bundle.putParcelableArrayList("cart",data.getCart_list());
                i.putExtra("mainActivity",bundle);
                startActivityForResult(i,1);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    flag=false;
                }else{
                    flag=true;
                }
                change();
            }
        });
    }
    private void Adapter_listener(){
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener(){
            @Override
            public void onClick(int position){
                if(position >= 0) {
                    Intent i = new Intent(MainActivity.this, Good_Info.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    bundle.putParcelableArrayList("data", data.getGood_list());
                    bundle.putParcelableArrayList("cart", data.getCart_list());
                    i.putExtra("mainActivity", bundle);
                    startActivityForResult(i, 0);
                }
            }
            @Override
            public void onLongClick(int position) {
                if (position >= 0) {
                    data.removeGood_list_index(position);
                    animationAdapter.notifyItemRemoved(position);
                    mRecyclerView.setItemViewCacheSize(data.getGood_list().size());
                    Toast.makeText(MainActivity.this, "移除了第"+String.valueOf(position)+"件商品", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void change(){
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }//布局切换
        if(flag) {
            mLayoutManager = new LinearLayoutManager(this);
            commonAdapter = new CommonAdapter(MainActivity.this, R.layout.items, data.getGood_list(), flag);
        }else{
            mLayoutManager = new GridLayoutManager(this, 2);
            commonAdapter = new CommonAdapter(MainActivity.this, R.layout.item2, data.getGood_list(), flag);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        animationAdapter = new ScaleInAnimationAdapter(commonAdapter);
        mRecyclerView.setAdapter(animationAdapter);//填充数据
        Adapter_listener();//建立监听
        mRecyclerView.scrollToPosition(scrollPosition);
    }
}
