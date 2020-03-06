package com.kritsin.holehint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;

public class HoleHintFragment extends AppCompatDialogFragment {

    private static final String TAG = HoleHintFragment.class.getSimpleName();

    private static final String POS_X = "POS_X";

    private static final String POS_Y = "POS_Y";

    private static final String WIDTH = "WIDTH";

    private static final String HEIGHT = "HEIGHT";

    private static final String LAYOUT_ID = "LAYOUT_ID";

    private static final String LINKED_VIEW_ID = "LINKED_VIEW_ID";

    private int backgroundColor = 0x80000000;

    private int dispatchColor = 0x00000000;

    private HoleView.ShapeType shapeType = HoleView.ShapeType.CIRCLE;

    @LayoutRes
    private int layoutId = 0;

    private int linkedViewId = 0;

    private View.OnClickListener fragmentClickListener, holeClickListener;

    private int posX, posY, width, height;

    private boolean dispatchTouchEnable = true;

    public static HoleHintFragment newInstance(@LayoutRes int layoutId, View linkedView) {
        HoleHintFragment holeHintFragment = new HoleHintFragment();

        int[] coords = new int[2];
        linkedView.getLocationInWindow(coords);

        Bundle bundle = new Bundle();
        bundle.putInt(POS_X, coords[0]);
        bundle.putInt(POS_Y, coords[1]);
        bundle.putInt(WIDTH, linkedView.getWidth());
        bundle.putInt(HEIGHT, linkedView.getHeight());
        bundle.putInt(LAYOUT_ID, layoutId);
        bundle.putInt(LINKED_VIEW_ID, linkedView.getId());

        holeHintFragment.setArguments(bundle);
        return holeHintFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        posX = bundle.getInt(POS_X);
        posY = bundle.getInt(POS_Y);
        width = bundle.getInt(WIDTH);
        height = bundle.getInt(HEIGHT);
        layoutId = bundle.getInt(LAYOUT_ID);
        linkedViewId = bundle.getInt(LINKED_VIEW_ID);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ConstraintLayout fragmentLayout;
        HoleView holeView;

        if (layoutId != 0) {
            fragmentLayout = (ConstraintLayout) inflater.inflate(layoutId, container, false);

            holeView = fragmentLayout.findViewById(linkedViewId);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)holeView.getLayoutParams();
            lp.leftToLeft = ConstraintSet.PARENT_ID;
            lp.topToTop = ConstraintSet.PARENT_ID;

            holeView.setLinkedView(posX, posY, width, height, shapeType);
        } else {
            fragmentLayout = new ConstraintLayout(getContext());

            holeView = new HoleView(getContext());
            holeView.setId(View.generateViewId());
            fragmentLayout.addView(holeView);

            holeView.setLinkedView(posX, posY, width, height, shapeType);
        }


        fragmentLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                View mainView = getDialog().getWindow().getDecorView();
                mainView.setDrawingCacheEnabled(true);
                mainView.buildDrawingCache();
                Bitmap bitmap = mainView.getDrawingCache();

                int x = (int) event.getX();
                int y = (int) event.getY() - getStatusBarHeight(getContext());

                if (y >= 0 && bitmap.getPixel(x, y) == dispatchColor && event.getAction() != MotionEvent.ACTION_MOVE) {
                    if (dispatchTouchEnable) {
                        getActivity().dispatchTouchEvent(event);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP && holeClickListener != null) {
//                        holeClickListener.onClick(holeView);
                    }
                }
                return false;
            }
        });

        fragmentLayout.setOnClickListener(fragmentClickListener);


        return fragmentLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getView().getLayoutParams();
        params.topMargin = -getStatusBarHeight(getContext());
        getView().setLayoutParams(params);
    }

    public HoleHintFragment setDispatchTouchEnable(boolean dispatchTouchEnable) {
        this.dispatchTouchEnable = dispatchTouchEnable;
        return this;
    }

    public HoleHintFragment setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public HoleHintFragment setDispatchColor(int dispatchColor) {
        this.dispatchColor = dispatchColor;
        return this;
    }

    public HoleHintFragment setOnFragmentClickListener(View.OnClickListener clickListener) {
        this.fragmentClickListener = clickListener;
        return this;
    }

    public HoleHintFragment setOnHoleClickListener(View.OnClickListener clickListener) {
        this.holeClickListener = clickListener;
        return this;
    }

    public HoleHintFragment setShapeType(HoleView.ShapeType shapeType) {
        this.shapeType = shapeType;
        return this;
    }

    public static void showDialog(FragmentManager fragmentManager, View linkedView, @LayoutRes int layoutId) {
        HoleHintFragment holeHintFragment = HoleHintFragment.newInstance(layoutId, linkedView);
        holeHintFragment.show(fragmentManager, TAG);
    }

    public static void showDialog(FragmentManager fragmentManager, View linkedView) {
        HoleHintFragment holeHintFragment = HoleHintFragment.newInstance(0, linkedView);
        holeHintFragment.show(fragmentManager, TAG);
    }

    public static int getSoftButtonsBarSizePort(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            display.getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            display.getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean isVisible(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            HoleHintFragment fragment = (HoleHintFragment) fragmentManager.findFragmentByTag(TAG);
            return fragment != null && fragment.isVisible();
        }
        return false;
    }

    public static void hide(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            HoleHintFragment fragment = (HoleHintFragment) fragmentManager.findFragmentByTag(TAG);
            if (fragment != null && fragment.isVisible()) {
                fragment.dismiss();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 android.view.KeyEvent event) {
                return (keyCode == android.view.KeyEvent.KEYCODE_BACK);
            }
        });
    }
}
