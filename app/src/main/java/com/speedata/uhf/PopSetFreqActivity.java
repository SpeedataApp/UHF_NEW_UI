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
import android.widget.Toast;

/**
 * 设置定频底部弹框
 * Created by 张智超 on 2019/3/5
 *
 * @author My_PC
 */
public class PopSetFreqActivity extends Activity {

    private ListView lvFreq;

    private String freq;

    /**
     * 其他模块频段
     */
    private final String[] freq_area_item = {"840-845", "920-925", "902-928", "865-868", "..."};
    /**
     * R2000模块频段
     */
    private final String[] r2k_freq_area_item = {"840-845", "920-925", "902-928",
            "865-868", "当前状态为定频", "..."};


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
        lvFreq = (ListView) findViewById(R.id.popup_set_lv);
        tvCancel.setOnClickListener(new Click());
        layout.setOnClickListener(new Click());
    }

    public void initData() {
        ArrayAdapter<String> tmp;
        tmp = new ArrayAdapter<>(PopSetFreqActivity.this, R.layout.item_set_popup, r2k_freq_area_item);
        lvFreq.setAdapter(tmp);

        lvFreq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        freq = "840-845";
                        break;
                    case 1:
                        freq = "920-925";
                        break;
                    case 2:
                        freq = "902-928";
                        break;
                    case 3:
                        freq = "865-868";
                        break;
                    case 4:
                        freq = "当前状态为定频";
                        break;
                    case 5:
                        freq = "...";
                        break;
                    default:
                        break;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("freq", freq);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public class Click implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_pop_cancel:
                    finish();//关闭该窗口
                    break;
                case R.id.pop_content:
                    //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
                    Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
     * @param event 触摸事件
     * @return 返回值
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
