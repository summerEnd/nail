package com.sp.lib.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sp.lib.R;
import com.sp.lib.util.DisplayUtil;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import static com.sp.lib.util.DisplayUtil.getScreenSize;

/**
 *
 */
public class PhotoAlbumActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private final String TAG = "PhotoAlbumActivity";
    GridView     grid;
    LinearLayout bottom_ll;
    /**
     * 图片目录
     */
    private String[] dirsNameArarry;
    /**
     * 图片目录和图片url集合
     */
    private       HashMap<String, LinkedList<String>> urlsMap                    = new HashMap<String, LinkedList<String>>();
    private final int                                 OPEN_CAMERA                = 100;
    private final int                                 CROP_IMAGE                 = 101;
    /**
     * Intent参数，拍照选的保存路径，String类型。如果不传，将不保存。
     */
    public static String                              EXTRA_CAMERA_OUTPUT_PATH   = "camera_out_path";
    /**
     * 输出图片的高度，单位dp
     */
    public static String                              EXTRA_CAMERA_OUTPUT_HEIGHT = "camera_out_height";
    /**
     * 输出图片的宽度，单位dp
     */
    public static String                              EXTRA_CAMERA_OUTPUT_WIDTH  = "camera_out_width";

    /**
     * 相机的输出uri
     */
    private Uri cameraUri;

    private int                outPut_height;
    private int                outPut_width;
    private LinkedList<String> curAlbumList;
    AlbumAdapter pictureAdapter;
    private int selectedItem = 0;
    private DirWindow dirWindow;
    private final String ALL   = "全部";
    private final String OTHER = "其他";
    DirAdapter dirAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_albumn);
        grid = (GridView) findViewById(R.id.grid);
        bottom_ll = (LinearLayout) findViewById(R.id.bottom_ll);
        loadPictures();
        initParams();
        bottom_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWindow();
            }
        });
    }

    /**
     * 初始化参数
     */
    void initParams() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        String camera_output = getIntent().getStringExtra(EXTRA_CAMERA_OUTPUT_PATH);
        outPut_height = (int) (getIntent().getIntExtra(EXTRA_CAMERA_OUTPUT_HEIGHT, 30) * dm.density);
        outPut_width = (int) (getIntent().getIntExtra(EXTRA_CAMERA_OUTPUT_WIDTH, 30) * dm.density);
        if (!TextUtils.isEmpty(camera_output))
            cameraUri = Uri.fromFile(new File(camera_output));
        else
            cameraUri = Uri.fromFile(ImageUtil.getImageTempFile("album"));
    }

    /**
     * 加载图片
     */
    @SuppressWarnings("deprecation")
    private void loadPictures() {
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_MODIFIED
        };
        Cursor cursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null,
                MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC");

        TreeSet<String> dirSet = new TreeSet<String>();
        LinkedList<String> totalList = new LinkedList<String>();
        while (cursor.moveToNext()) {
            String url = cursor.getString(0);
            String dir = url.replace("/mnt", "")
                    .replace("/sdcard/", "")
                    .replace("/ext_sdcard/", "")
                    .replace("/sdcard0/", "");
            try {
                dir = dir.substring(0, dir.indexOf('/'));
            } catch (Exception e) {
                dir = OTHER;
            }
            LinkedList tempList = urlsMap.get(dir);
            if (tempList == null) {
                tempList = new LinkedList();
            }
            tempList.add(url);
            totalList.add(url);
            dirSet.add(dir);
            urlsMap.put(dir, tempList);
        }
        dirsNameArarry = new String[dirSet.size() + 1];
        dirSet.toArray(dirsNameArarry);
        dirsNameArarry[dirsNameArarry.length - 1] = ALL;
        urlsMap.put(ALL, totalList);
        if (Build.VERSION.SDK_INT < 14) {
            //在14以上版本，cursor会自动关闭，如果手动关闭在resume的时候会抛出异常。
            cursor.close();
        }
        curAlbumList = urlsMap.get(dirsNameArarry[0]);
        pictureAdapter = new AlbumAdapter(this);
        grid.setAdapter(pictureAdapter);
        grid.setOnItemClickListener(this);
    }

    /**
     * 创建popupWindow
     *
     * @return
     */
    private void showWindow() {
        if (dirWindow == null) {
            dirWindow = new DirWindow(this);
            dirAdapter = new DirAdapter(PhotoAlbumActivity.this);
            dirWindow.setListAdapter(dirAdapter);
            dirWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //获取点击的目录名称
                    String dirName = dirsNameArarry[position];
                    //获取目录对应的图片列表
                    curAlbumList = urlsMap.get(dirName);
                    //设置标题为目录名称
                    setTitle(dirName);
                    pictureAdapter.notifyDataSetChanged();
                    dirAdapter.select(position);
                    dirWindow.exitWithAnimation();
                }
            });
        }
        dirWindow.showWithAnimation(bottom_ll);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            //打开相机拍照
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            }
            startActivityForResult(intent, OPEN_CAMERA);
        } else {//选择照片，返回照片Uri
            File selected = new File(curAlbumList.get(position - 1));
            cropImage(Uri.fromFile(selected));
        }
    }

    /**
     * 返回图片uri
     *
     * @param uri
     */
    private void returnUri(Uri uri) {
        Intent data = new Intent();
        data.setData(uri);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
        } else if (requestCode == OPEN_CAMERA) {
            Log.i(TAG, "CROP_IMAGE output:" + cameraUri);
            cropImage(cameraUri);
        } else if (requestCode == CROP_IMAGE) {
            returnUri(cameraUri);
        }
    }

    /**
     * 剪裁图片
     *
     * @param uri
     */
    private void cropImage(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", outPut_width);
        intent.putExtra("aspectY", outPut_height);
        intent.putExtra("outputX", outPut_width);
        intent.putExtra("outputY", outPut_height);
        intent.putExtra("output", cameraUri);// 保存到原文件
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CROP_IMAGE);
    }

    class AlbumAdapter extends BaseAdapter {
        private Context mContext;

        DisplayImageOptions options;
        View                header;
        int                 width;

        @SuppressWarnings("deprecation")
        AlbumAdapter(Context context) {
            this.mContext = context;
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .showImageForEmptyUri(R.drawable.fail_image_square)
                    .showImageOnFail(R.drawable.fail_image_square)
                    .build();
            header = LayoutInflater.from(mContext).inflate(R.layout.camera_header, null);
            Point p = new Point();
            getScreenSize((android.app.Activity) context, p);
            width = (int) (p.x / 3 - DisplayUtil.dp(1, context.getResources()));
            header.setLayoutParams(new AbsListView.LayoutParams(width, width));
        }

        @Override
        public int getCount() {
            return curAlbumList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return "";
            } else {
                return curAlbumList.get(position - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                convertView = header;
                header.setTag(null);
            } else {
                ViewHolder holder;
                if (convertView == null || convertView.getTag() == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_photo, null);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                    holder.imageView.setLayoutParams(new AbsListView.LayoutParams(width, width));
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                String imageUrl = curAlbumList.get(position - 1);
                ImageManager.loadImage("file://" + imageUrl, holder.imageView, options);
                convertView.setTag(holder);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView imageView;
    }

    class DirAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
        DisplayImageOptions options;
        Context             context;

        DirAdapter(Context context) {
            this.context = context;
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .showImageForEmptyUri(R.drawable.fail_image_square)
                    .showImageOnFail(R.drawable.fail_image_square)
                    .build();
        }

        public void select(int position) {
            selectedItem = position;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dirsNameArarry.length;
        }

        @Override
        public Object getItem(int position) {
            return dirsNameArarry[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder2 holder;
            if (convertView == null) {
                holder = new ViewHolder2();
                convertView = LayoutInflater.from(context).inflate(R.layout.photo_album_window_list_item, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
                holder.dir_name = (TextView) convertView.findViewById(R.id.dir_name);
                holder.num_pics = (TextView) convertView.findViewById(R.id.num_pics);
                holder.selected = (ImageView) convertView.findViewById(R.id.selected);
                //                holder.selected.setOnCheckedChangeListener(this);
            } else {
                holder = (ViewHolder2) convertView.getTag();
            }
            //            LogCat.d("dir-->" + dirsNameArarry);
            String dirName = dirsNameArarry[position];
            LinkedList<String> list = urlsMap.get(dirName);
            if (list.size() > 0) {
                String image = list.get(0);
                ImageManager.loadImage("file://" + image, holder.imageView, options);
            }
            holder.dir_name.setText(dirName);
            holder.num_pics.setText(list.size() + "张");
            holder.selected.setVisibility(selectedItem == position ? View.VISIBLE : View.INVISIBLE);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            selectedItem = (Integer) buttonView.getTag();
            notifyDataSetChanged();
            pictureAdapter.notifyDataSetChanged();
            dirWindow.dismiss();
        }
    }

    static class ViewHolder2 {
        ImageView imageView;
        TextView  dir_name;
        TextView  num_pics;
        ImageView selected;
    }

    /**
     * 图片目录展示窗口
     */
    static class DirWindow extends PopupWindow implements AdapterView.OnItemClickListener {
        View                            contentView;
        ListView                        list;
        AdapterView.OnItemClickListener mExtraOnItemClick;
        Context                         context;

        DirWindow(Context context) {
            super(context);
            this.context = context;
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setBackgroundDrawable(new ColorDrawable(0x80000000));
            contentView = View.inflate(context, R.layout.photo_ablum_layout, null);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitWithAnimation();
                }
            });
            list = (ListView) contentView.findViewById(R.id.listView);
            list.setOnItemClickListener(this);
            setContentView(contentView);
        }

        /**
         * 设置列表Adapter
         *
         * @param adapter
         */
        public void setListAdapter(BaseAdapter adapter) {
            list.setAdapter(adapter);
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            mExtraOnItemClick = onItemClickListener;
        }

        /**
         * 动画弹出
         *
         * @param v
         */
        public void showWithAnimation(View v) {
            showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_up_in);
            animation.setDuration(200);
            list.startAnimation(animation);
        }

        /**
         * 动画退出
         */
        public void exitWithAnimation() {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down_out);
            animation.setDuration(200);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            list.startAnimation(animation);
        }


        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            if (mExtraOnItemClick != null) {
                mExtraOnItemClick.onItemClick(parent, view, position, id);
            }
        }
    }
}