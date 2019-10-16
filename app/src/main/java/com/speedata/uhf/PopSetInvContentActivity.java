package com.speedata.uhf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 设置盘点内容底部弹框
 * Created by 张智超 on 2019/3/12
 *
 * @author My_PC
 */
public class PopSetInvContentActivity extends BaseActivity {

    private ListView lvInvCon;

    private String invCon;

    private String[] slist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_set_popup);
        // 让此界面的宽度撑满整个屏幕
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
        initData();
    }

    public void initView() {
        TextView tvCancel = (TextView) findViewById(R.id.tv_pop_cancel);
        LinearLayout layout = (LinearLayout) findViewById(R.id.pop_content);
        lvInvCon = (ListView) findViewById(R.id.popup_set_lv);
        tvCancel.setOnClickListener(new click());
        layout.setOnClickListener(new click());
    }

    public void initData() {
        slist = getResources().getStringArray(R.array.inv_con);
        ArrayAdapter<String> tmp;
        tmp = new ArrayAdapter<>(PopSetInvContentActivity.this, R.layout.item_set_popup, slist);
        lvInvCon.setAdapter(tmp);

        lvInvCon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invCon = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("InvCon", invCon);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public class click implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_pop_cancel:
                    finish();//关闭该窗口
                    break;
                case R.id.pop_content:
                    //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param event 点击
     * @return 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
