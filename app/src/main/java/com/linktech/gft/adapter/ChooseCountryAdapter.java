package com.linktech.gft.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.linktech.gft.R;
import com.linktech.gft.bean.CountryBean;

import java.util.List;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
public class ChooseCountryAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;

    private List<CountryBean> list;

    public ChooseCountryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CountryBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (list != null) return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list == null)
            return null;

        if (position >= list.size())
            return null;

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        CountryBean countryBean = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_country, parent, false);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvNationCode = convertView.findViewById(R.id.tv_nationCode);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvTitle.setVisibility(View.VISIBLE);
            String letterFirst = countryBean.getLetters();
            if (!TextUtils.isEmpty(letterFirst)) {
                letterFirst = String.valueOf(letterFirst.toUpperCase().charAt(0));
            }
            viewHolder.tvTitle.setText(letterFirst);
        } else {
            viewHolder.tvTitle.setVisibility(View.GONE);
        }
        viewHolder.tvName.setText(this.list.get(position).getCountry());
        viewHolder.tvNationCode.setText("+" + this.list.get(position).getCodeArea());
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getLetters();
            char firstChar = sortStr.charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getLetters().charAt(0);
    }


    static final class ViewHolder {
        /**
         * 首字母
         */
        TextView tvName;
        /**
         * 昵称
         */
        TextView tvTitle;
        /**
         * 国家码
         */
        TextView tvNationCode;
    }
}
