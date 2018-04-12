package com.graduation_project.android.algebrablade;


import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.graduation_project.android.algebrablade.model.CurveSource;
import com.graduation_project.android.algebrablade.model.CurveSourceLab;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GraphicEditActivity extends AppCompatActivity {
    @BindView(R.id.graphic_edit_recycler_view)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_edit);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new GraphicEditAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_graphic_edit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.draw_curve: {

            } break;

            default: break;
        }

        return true;
    }

    private class GraphicEditAdapter extends RecyclerView.Adapter<GraphicEditAdapter.ViewHolder> {
        private final int TYPE_ITEM = 0;
        private final int TYPE_ADD_BUTTON = 1;

        private int mCurrentCurveId = 0;
        private SparseArray<CurveSource> mCurveSources = CurveSourceLab.getInstance().getCurveSources();


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case TYPE_ITEM: {

                    return new ItemViewHolder(view);
                }

                case TYPE_ADD_BUTTON: {

                    return new AddButtonHolder(view);
                }

                default: break;
            }

            return null; //不可能走到这，但是也添加上这条语句
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mCurveSources.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return TYPE_ADD_BUTTON;
            }

            return TYPE_ITEM;
        }


        abstract class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            abstract void bindData();
        }

        private class ItemViewHolder extends ViewHolder {
            public ItemViewHolder(View itemView) {
                super(itemView);
            }

            @Override
            void bindData() {

            }
        }

        private class AddButtonHolder extends ViewHolder {
            public AddButtonHolder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

            @Override
            void bindData() {

            }
        }
    }
}
