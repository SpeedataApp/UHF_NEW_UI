package com.speedata.uhf;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.utils.CommonUtils;
import com.speedata.uhf.dialog.ReadCardDialog;
import com.speedata.uhf.dialog.WriteCardDialog;

import java.util.Objects;

/**
 * 当前卡片
 * The current card
 * Created by 张智超 on 2019/3/7
 *
 * @author 张智超
 */
public class CurrentCardActivity extends BaseActivity implements View.OnClickListener {

    private String epcName;
    private Spinner areaSelect;
    private TextView mVersionTv;

    private String model;
    private IUHFService iuhfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //强制为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.select_one_card);
        getData();
        initView();
        mVersionTv.append("-" + model);
        iuhfService = MyApp.getInstance().getIuhfService();

    }

    public void initView() {
        TextView epcNameTv = (TextView) findViewById(R.id.tv_epc_name);
        LinearLayout mLlReadEpc = (LinearLayout) findViewById(R.id.ll_read_epc);
        LinearLayout mLlWriteEpc = (LinearLayout) findViewById(R.id.ll_write_epc);
        LinearLayout mLlCardAttrSet = (LinearLayout) findViewById(R.id.ll_card_attr_set);
        ImageView mIvExit = (ImageView) findViewById(R.id.title_exit);
        mVersionTv = (TextView) findViewById(R.id.tv_version_model);
        mVersionTv.setText(CommonUtils.getAppVersionName(this));
        mLlReadEpc.setOnClickListener(this);
        mLlWriteEpc.setOnClickListener(this);
        mLlCardAttrSet.setOnClickListener(this);
        mIvExit.setOnClickListener(this);
        areaSelect = (Spinner) findViewById(R.id.sp_title);
        areaSelect.setSelection(2,true);
        areaSelect.setSelection(0);
        areaSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view;
                //设置颜色
                tv.setTextColor(getBaseContext().getResources().getColor(R.color.text_white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        epcNameTv.setText(epcName);
    }

    public void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        assert bundle != null;
        epcName = Objects.requireNonNull(bundle).getString("epcName");
        model = bundle.getString("model");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_card_attr_set:
                //设置
                Intent intent = new Intent(this, PopAttrSetActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_read_epc:
                //读卡
                ReadCardDialog readCard = new ReadCardDialog(this, iuhfService
                        , areaSelect.getSelectedItemPosition(), epcName, model);
                readCard.setTitle(R.string.Item_Read);
                readCard.show();
                break;
            case R.id.ll_write_epc:
                //写卡
                WriteCardDialog writeCard = new WriteCardDialog(this, iuhfService,
                        areaSelect.getSelectedItemPosition()
                        , epcName, model);
                writeCard.setTitle(R.string.Item_Write);
                writeCard.show();
                break;
            case R.id.title_exit:
                //返回
                finish();
                break;
            default:
                break;
        }
    }
}
