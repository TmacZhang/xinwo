package com.xinwo.produce.music.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xinwo.produce.R;
import com.xinwo.produce.music.bean.MusicBean;
import com.xinwo.produce.music.bean.MusicCategoryBean;
import com.xinwo.produce.music.ui.adapter.MusicAdapter;
import com.xinwo.produce.music.ui.adapter.MusicCategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private SearchView svMusic;
    private RecyclerView rvCategory;
    private TextView tvHot;
    private TextView tvFavorite;
    private RecyclerView rvMusic;
    private List<MusicCategoryBean> musicCategoryBeanListShort;
    private List<MusicCategoryBean> musicCategoryBeanListFull;
    private MusicCategoryAdapter musicCategoryAdapter;
    private MusicAdapter musicAdapter;
    private List<MusicBean> hotMusicBeanList;
    private List<MusicBean> favotiteMusicBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initData();
        initView();
        initAdapter();
    }

    private void initData() {
        //音乐分类
        musicCategoryBeanListShort = new ArrayList<>();
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_hot, "热门"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_popular, "流行"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_electric, "电音"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_america, "欧美"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_pet, "萌宠"));

        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_hot, "热门"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_popular, "流行"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_electric, "电音"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_america, "欧美"));
        musicCategoryBeanListShort.add(new MusicCategoryBean(R.mipmap.music_category_more, "更多"));


        //热门音乐
        MusicBean musicBean = new MusicBean();
        musicBean.name = "往后余生";
        musicBean.author = "author";
        musicBean.duration = "00:14";
        musicBean.isFavorite = false;

        hotMusicBeanList = new ArrayList<>();
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);
        hotMusicBeanList.add(musicBean);

        //我的收藏
        MusicBean musicBean2 = new MusicBean();
        musicBean2.name = "默";
        musicBean2.author = "author";
        musicBean2.duration = "00:15";
        musicBean2.isFavorite = true;
        favotiteMusicBeanList = new ArrayList<>();
        favotiteMusicBeanList.add(musicBean2);
        favotiteMusicBeanList.add(musicBean2);
        favotiteMusicBeanList.add(musicBean2);
        favotiteMusicBeanList.add(musicBean2);
        favotiteMusicBeanList.add(musicBean2);
        favotiteMusicBeanList.add(musicBean2);
    }


    private void initView() {
        findViewById(R.id.ivBack).setOnClickListener(this);

        svMusic = (SearchView) findViewById(R.id.svMusic);
        rvCategory = (RecyclerView) findViewById(R.id.rvCategory);

        tvHot = (TextView) findViewById(R.id.tvHot);
        tvHot.setTextColor(Color.parseColor("#ED3649"));
        tvHot.setOnClickListener(this);

        tvFavorite = (TextView) findViewById(R.id.tvFavorite);
        tvFavorite.setOnClickListener(this);

        rvMusic = (RecyclerView) findViewById(R.id.rvMusic);    }

    private void initAdapter() {
        musicCategoryAdapter = new MusicCategoryAdapter(musicCategoryBeanListShort);
        musicCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        if(musicCategoryAdapter.isShort()){
                            musicCategoryBeanListFull = new ArrayList<MusicCategoryBean>();
                            
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_hot, "热门"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_popular, "流行"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_electric, "电音"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_america, "欧美"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_pet, "萌宠"));

                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_hot, "热门"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_popular, "流行"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_electric, "电音"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_america, "欧美"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_pet, "萌宠"));

                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_hot, "热门"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_popular, "流行"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_electric, "电音"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_america, "欧美"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_pet, "萌宠"));

                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_hot, "热门"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_popular, "流行"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_electric, "电音"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_america, "欧美"));
                            musicCategoryBeanListFull.add(new MusicCategoryBean(R.mipmap.music_category_pet, "萌宠"));
                            
                            musicCategoryAdapter.setShort(false);
                            musicCategoryAdapter.setNewData(musicCategoryBeanListFull);
                        }else{

                        }
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                    case 12:
                        break;
                    case 13:
                        break;
                    case 14:
                        break;
                    case 15:
                        break;
                    case 16:
                        break;
                    case 17:
                        break;
                    case 18:
                        break;
                    case 19:
                        break;
                }
            }
        });
        rvCategory.setLayoutManager(new GridLayoutManager(getBaseContext(), 5));
        rvCategory.setAdapter(musicCategoryAdapter);


        musicAdapter = new MusicAdapter();
        musicAdapter.setNewData(hotMusicBeanList);
        rvMusic.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvMusic.setAdapter(musicAdapter);
        musicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                musicAdapter.setItemClickPosition(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivBack) {
            finish();
        } else if (id == R.id.tvHot) {
            tvHot.setTextColor(Color.parseColor("#ED3649"));
            tvFavorite.setTextColor(Color.parseColor("#FFFFFF"));
            musicAdapter.setNewData(hotMusicBeanList);
        } else if (id == R.id.tvFavorite) {
            tvHot.setTextColor(Color.parseColor("#FFFFFF"));
            tvFavorite.setTextColor(Color.parseColor("#ED3649"));
            musicAdapter.setNewData(favotiteMusicBeanList);
        }
    }
}
