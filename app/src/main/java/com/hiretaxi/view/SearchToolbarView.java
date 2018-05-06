package com.hiretaxi.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiretaxi.R;
import com.hiretaxi.util.KeyBoardUtils;
import com.hiretaxi.util.StringUtil;
import com.hiretaxi.util.ToastUtil;


/**
 * Created by Administrator on 2017/6/12.
 */
public class SearchToolbarView extends FrameLayout {

    private ImageView ivLeft, ivDelete;
    private TextView tvRight;
    private EditText etSearch;
    private View rootView;

    public EditText getEtSearch() {
        return etSearch;
    }

    public SearchToolbarView(Context context) {
        this(context, null);
    }

    public SearchToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.toolbar_search_lay, null);
        ivLeft = (ImageView) rootView.findViewById(R.id.iv_left);
        ivDelete = (ImageView) rootView.findViewById(R.id.iv_delete);
        tvRight = (TextView) rootView.findViewById(R.id.tv_right);
        etSearch = (EditText) rootView.findViewById(R.id.et_search);
        setListener();
        this.addView(rootView);
    }

    private void setListener() {
        ivLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headerLsitener != null) {
                    headerLsitener.clickLeftListener();
                } else {
                    ((Activity)getContext()).finish();
                }
            }
        });

        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headerLsitener != null) {
                    String key = etSearch.getText().toString();
                    if (StringUtil.isEmpty(key)) {
                        ToastUtil.showToast(getContext(), "输入不能为空");
                        return;
                    }
                    headerLsitener.clickRightListener(key);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    int len = s.length();
                    if (len > 0) {
                        ivDelete.setVisibility(View.VISIBLE);
                    } else {
                        ivDelete.setVisibility(View.GONE);
                    }
                }
            }
        });

        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                KeyBoardUtils.closeKeybord(etSearch, getContext());
            }
        });
    }


    public void setIvLeft(int ivLeftId) {
        if (ivLeftId != 0) {
            ivLeft.setImageResource(ivLeftId);
        }
    }


    public void setVisibity(boolean leftViewVisibity, boolean rightViewVisibity) {
        if (leftViewVisibity) {
            ivLeft.setVisibility(View.VISIBLE);
        } else {
            ivLeft.setVisibility(View.GONE);
        }

        if (rightViewVisibity) {
            tvRight.setVisibility(View.VISIBLE);
        } else {
            tvRight.setVisibility(View.GONE);
        }
    }

    public interface HeaderLsitener {
        void clickLeftListener();

        void clickRightListener(String searchKey);
    }

    private HeaderLsitener headerLsitener;

    public void setHeaderLsitener(HeaderLsitener headerLsitener) {
        this.headerLsitener = headerLsitener;
    }
}
