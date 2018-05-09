package com.graduation_project.android.algebrablade;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import com.daoshengwanwu.math_util.calculator.Variable;
import com.graduation_project.android.algebrablade.model.CurveSource;
import com.graduation_project.android.algebrablade.model.CurveSourceLab;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GraphicEditActivity extends AppCompatActivity {
    @BindView(R.id.graphic_edit_recycler_view)
    RecyclerView mRecyclerView;


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, GraphicEditActivity.class);

        return intent;
    }

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
                startActivity(GraphicActivity.newIntent(this));
            } break;

            case android.R.id.home: {
                finish();
            } break;

            default: break;
        }

        return true;
    }

    class GraphicEditAdapter extends RecyclerView.Adapter<GraphicEditAdapter.ViewHolder> {
        private final int TYPE_ITEM = 0;
        private final int TYPE_ADD_BUTTON = 1;

        private SparseArray<CurveSource> mCurveSources = CurveSourceLab.getInstance().getCurveSources();
        private int mCurrentCurveId = mCurveSources.size() > 0 ?
                mCurveSources.keyAt(mCurveSources.size() - 1) + 1 : 0;


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case TYPE_ITEM: {
                    view = inflater.inflate(R.layout.item_curve, parent, false);
                    return new ItemViewHolder(view);
                }

                case TYPE_ADD_BUTTON: {
                    view = inflater.inflate(R.layout.item_add_curve_button, parent, false);
                    return new AddButtonHolder(view);
                }

                default: break;
            }

            return null; //不可能走到这，但是也添加上这条语句
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData();
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

        private CurveSource getCurveSourceItem(int position) {
            return mCurveSources.valueAt(position);
        }

        private int getCurveIdItem(int position) {
            return mCurveSources.keyAt(position);
        }


        abstract class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            abstract void bindData();
        }

        class ItemViewHolder extends ViewHolder {
            private int mId;
            private CurveSource mCurveSource;

            @BindView(R.id.curve_id_text_view)
            TextView mCurveIdTextView;

            @BindView(R.id.curve_color_text_view)
            TextView mCurveColorTextView;

            @BindView(R.id.expression_text_view)
            TextView mExpressionTextView;

            @BindView(R.id.domain_text_view)
            TextView mDomainTextView;

            @BindView(R.id.delta_text_view)
            TextView mDeltaTextView;


            public ItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new GraphicEditDialogBuilder(mId, mCurveSource, GraphicEditActivity.this)
                        .setOnConfirmClickListener(new GraphicEditDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(int id, CurveSource curveSource) {
                                notifyItemChanged(getAdapterPosition());
                            }
                        }).setOnCancelClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setOnDeleteClickListener(new GraphicEditDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(int id, CurveSource curveSource) {
                                mCurveSources.removeAt(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            }
                        }).build().show();
                    }
                });
            }

            @Override
            void bindData() {
                mId = getCurveIdItem(getAdapterPosition());
                mCurveSource = getCurveSourceItem(getAdapterPosition());
                mCurveIdTextView.setText("" + mId);
                mCurveColorTextView.setBackgroundColor(mCurveSource.color);

                if (mCurveSource.varAriExp != null) {
                    Variable x = mCurveSource.varAriExp.getVariableAssistant().getVariable("x");
                    mExpressionTextView.setText("y = " + mCurveSource.varAriExp.toString());
                    if (mCurveSource.isDomainSet) {
                        mDomainTextView.setText("[" + x.getLowerLimit() + "," + x.getUpperLimit() + "]");
                        mDeltaTextView.setText("delta: " + x.getSpan());
                    } else {
                        mDomainTextView.setText("自动填充定义域");
                        mDeltaTextView.setText("");
                    }
                } else {
                    mExpressionTextView.setText("");
                    mDomainTextView.setText("");
                    mDeltaTextView.setText("");
                }
            }
        }

        class AddButtonHolder extends ViewHolder {
            @BindView(R.id.add_curve_button)
            Button mButton;


            public AddButtonHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = mCurrentCurveId++;
                        CurveSource curveSource = new CurveSource(null, false, CurveSourceLab.getRandomColor());
                        mCurveSources.put(id, curveSource);
                        notifyItemInserted(mCurveSources.indexOfKey(id));
                        mRecyclerView.getLayoutManager().scrollToPosition(getAdapterPosition());

                        new GraphicEditDialogBuilder(id, curveSource, GraphicEditActivity.this)
                                .setOnConfirmClickListener(new GraphicEditDialogBuilder.OnClickListener() {
                                    @Override
                                    public void onClick(int id, CurveSource curveSource) {
                                        notifyItemChanged(mCurveSources.indexOfKey(id));
                                    }
                                }).setOnCancelClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setOnDeleteClickListener(new GraphicEditDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(int id, CurveSource curveSource) {
                                notifyItemRemoved(mCurveSources.indexOfKey(id));
                                mCurveSources.remove(id);
                            }
                        }).build().show();
                    }
                });
            }

            @Override
            void bindData() {
                //do nothing
            }
        }
    }
}
