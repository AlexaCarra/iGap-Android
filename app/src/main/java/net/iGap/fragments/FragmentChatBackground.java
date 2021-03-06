/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterChatBackground;
import net.iGap.adapter.AdapterSolidChatBackground;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnGetWallpaper;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.DialogAnimation;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperProto;
import net.iGap.request.RequestInfoWallpaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.DIR_CHAT_BACKGROUND;

public class FragmentChatBackground extends BaseFragment {

    private String savePath;
    private RippleView rippleBack;
    private RippleView rippleSet;
    private RippleView rippleSetDefault;
    private RecyclerView mRecyclerView, rcvSolidColor;
    private ImageView imgFullImage;
    private AdapterChatBackground adapterChatBackgroundSetting;
    private AdapterSolidChatBackground adapterSolidChatbackground;
    private ArrayList<StructWallpaper> wList;
    private Realm realmChatBackground;
    private Fragment fragment;
    private RippleView chB_ripple_menu_button;

    ArrayList<String> solidList = new ArrayList<>();


    public static FragmentChatBackground newInstance() {
        return new FragmentChatBackground();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmChatBackground = Realm.getDefaultInstance();
        return attachToSwipeBack(inflater.inflate(R.layout.activity_chat_background, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            new File(DIR_CHAT_BACKGROUND).mkdirs();
            new File(DIR_CHAT_BACKGROUND + "/.nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fragment = this;
        view.findViewById(R.id.stcb_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        rippleBack = (RippleView) view.findViewById(R.id.stcb_ripple_back);

        chB_ripple_menu_button = (RippleView) view.findViewById(R.id.chB_ripple_menu_button);

        chB_ripple_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
                View v = dialog.getCustomView();

                DialogAnimation.animationUp(dialog);
                dialog.show();

                ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
                ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

                TextView txtSolidColors = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
                TextView txtWallpaper = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

                TextView iconSolidColor = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
                iconSolidColor.setText(G.fragmentActivity.getResources().getString(R.string.md_solid_colors));



                TextView iconWallpaper = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);

                iconWallpaper.setText(G.fragmentActivity.getResources().getString(R.string.md_wallpapers));

                root1.setVisibility(View.VISIBLE);
                root2.setVisibility(View.VISIBLE);

                txtSolidColors.setText(getResources().getString(R.string.solid_colors));
                txtWallpaper.setText(getResources().getString(R.string.wallpapers));

                root1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mRecyclerView.setVisibility(View.GONE);
                        rcvSolidColor.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

                root2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rcvSolidColor.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
            }
        });

        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        imgFullImage = (ImageView) view.findViewById(R.id.stchf_fullImage);

        SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(backGroundPath), imgFullImage);
            }
        }

        rippleSetDefault = (RippleView) view.findViewById(R.id.stcbf_ripple_set_default);

        rippleSetDefault.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {

                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
                editor.apply();

                popBackStackFragment();
            }
        });

        rippleSet = (RippleView) view.findViewById(R.id.stcbf_ripple_set);
        rippleSet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (savePath != null && savePath.length() > 0) {
                    if (G.twoPaneMode && G.onBackgroundChanged != null) {
                        G.onBackgroundChanged.onBackgroundChanged(savePath);
                    }
                    SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, savePath);
                    editor.apply();
                    popBackStackFragment();
                }
            }
        });

        fillList(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcvContent);
        rcvSolidColor = (RecyclerView) view.findViewById(R.id.rcvSolidColor);


        adapterChatBackgroundSetting = new AdapterChatBackground(fragment, wList, new OnImageClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String imagePath) {

                G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgFullImage);


                savePath = imagePath;

                rippleSet.setVisibility(View.VISIBLE);
                rippleSetDefault.setVisibility(View.GONE);
            }
        });

        adapterSolidChatbackground = new AdapterSolidChatBackground(fragment, solidList, new OnImageClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String imagePath) {

                //   G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgFullImage);
                imgFullImage.setImageDrawable(null);
                imgFullImage.setBackgroundColor(Color.parseColor(imagePath));

                savePath = imagePath;

                rippleSet.setVisibility(View.VISIBLE);
                rippleSetDefault.setVisibility(View.GONE);
            }
        });


        mRecyclerView.setAdapter(adapterChatBackgroundSetting);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.clearAnimation();


        rcvSolidColor.setAdapter(adapterSolidChatbackground);
        rcvSolidColor.setLayoutManager(new LinearLayoutManager(G.fragmentActivity, LinearLayoutManager.HORIZONTAL, false));
        rcvSolidColor.clearAnimation();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        String filePath = null;

        switch (requestCode) {
            case AttachFile.request_code_TAKE_PICTURE:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                    filePath = AttachFile.mCurrentPhotoPath;
                } else {
                    ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                    filePath = AttachFile.imagePath;
                }
                break;
            case AttachFile.request_code_image_from_gallery_single_select:

                if (data != null && data.getData() != null) {

                    if (G.fragmentActivity != null) {
                        AttachFile attachFile = new AttachFile(G.fragmentActivity);
                        filePath = attachFile.saveGalleryPicToLocal(AttachFile.getFilePathFromUri(data.getData()));
                    }
                }

                break;
        }

        if (filePath != null) {

            if (new File(filePath).exists()) {
                RealmWallpaper.updateField(null, filePath);

                fillList(false);

                adapterChatBackgroundSetting.notifyItemInserted(1);
            }
        }
    }

    private Realm getRealmChatBackground() {
        if (realmChatBackground == null || realmChatBackground.isClosed()) {
            realmChatBackground = Realm.getDefaultInstance();
        }
        return realmChatBackground;
    }

    private void getImageListFromServer() {
        G.onGetWallpaper = new OnGetWallpaper() {
            @Override
            public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {
                RealmWallpaper.updateField(list, "");
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillList(false);
                        adapterChatBackgroundSetting.notifyDataSetChanged();
                        adapterSolidChatbackground.notifyDataSetChanged();
                    }
                });
            }
        };

        new RequestInfoWallpaper().infoWallpaper();
    }

    private void fillList(boolean getInfoFromServer) {

        if (wList == null) wList = new ArrayList<>();

        wList.clear();


        //add item 0 add new background from local
        StructWallpaper sw = new StructWallpaper();
        sw.setWallpaperType(WallpaperType.addNew);
        wList.add(sw);

        Realm realm = Realm.getDefaultInstance();

        RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).findFirst();

        if (realmWallpaper != null) {

            if (realmWallpaper.getLocalList() != null) {
                for (String localPath : realmWallpaper.getLocalList()) {
                    if (new File(localPath).exists()) {
                        StructWallpaper _swl = new StructWallpaper();
                        _swl.setWallpaperType(WallpaperType.local);
                        _swl.setPath(localPath);
                        wList.add(_swl);

                    }
                }
            }

            if (realmWallpaper.getWallPaperList() != null) {
                for (RealmWallpaperProto wallpaper : realmWallpaper.getWallPaperList()) {
                    StructWallpaper _swp = new StructWallpaper();
                    _swp.setWallpaperType(WallpaperType.proto);
                    _swp.setProtoWallpaper(wallpaper);
                    wList.add(_swp);
                    solidList.add(_swp.getProtoWallpaper().getColor());
                }
            }

            if (getInfoFromServer) {

                long time = realmWallpaper.getLastTimeGetList();
                if (time > 0) {

                    if (time + (2 * 60 * 60 * 1000) < TimeUtils.currentLocalTime()) {
                        getImageListFromServer();
                    }
                } else {
                    getImageListFromServer();
                }
            }
        } else {
            if (getInfoFromServer) {
                getImageListFromServer();
            }
        }

        realm.close();
    }

    public enum WallpaperType {
        addNew, local, proto
    }

    public interface OnImageClick {
        void onClick(String imagePath);
    }

    public class StructWallpaper {

        private WallpaperType wallpaperType;
        private String path;
        private RealmWallpaperProto protoWallpaper;


        public WallpaperType getWallpaperType() {
            return wallpaperType;
        }

        public void setWallpaperType(WallpaperType wallpaperType) {
            this.wallpaperType = wallpaperType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public RealmWallpaperProto getProtoWallpaper() {
            return protoWallpaper;
        }

        public void setProtoWallpaper(RealmWallpaperProto mProtoWallpaper) {

            this.protoWallpaper = mProtoWallpaper;

        }
    }
}
