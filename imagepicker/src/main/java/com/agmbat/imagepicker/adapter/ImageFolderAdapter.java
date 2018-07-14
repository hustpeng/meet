package com.agmbat.imagepicker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.imagepicker.ImagePicker;
import com.agmbat.imagepicker.R;
import com.agmbat.imagepicker.bean.ImageFolder;
import com.agmbat.imagepicker.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ImageFolderAdapter extends BaseAdapter {

    private ImagePicker imagePicker;
    private Context mContext;
    private int mImageSize;
    private List<ImageFolder> imageFolders;
    private int lastSelected = 0;

    public ImageFolderAdapter(Context activity, List<ImageFolder> folders) {
        mContext = activity;
        if (folders != null && folders.size() > 0) {
            imageFolders = folders;
        } else {
            imageFolders = new ArrayList<>();
        }
        imagePicker = ImagePicker.getInstance();
        mImageSize = Utils.getImageItemWidth(mContext);
    }

    public void refreshData(List<ImageFolder> folders) {
        if (folders != null && folders.size() > 0) {
            imageFolders = folders;
        } else {
            imageFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imageFolders.size();
    }

    @Override
    public ImageFolder getItem(int position) {
        return imageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_folder_list_item, parent);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageFolder folder = getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(mContext.getString(R.string.ip_folder_image_count, folder.images.size()));
        imagePicker.getImageLoader().displayImage(mContext, folder.cover.path, holder.cover, mImageSize, mImageSize);

        if (lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    private static class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.iv_cover);
            folderName = (TextView) view.findViewById(R.id.tv_folder_name);
            imageCount = (TextView) view.findViewById(R.id.tv_image_count);
            folderCheck = (ImageView) view.findViewById(R.id.iv_folder_check);
            view.setTag(this);
        }
    }
}
