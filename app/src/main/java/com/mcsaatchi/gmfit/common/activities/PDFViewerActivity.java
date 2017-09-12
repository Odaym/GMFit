package com.mcsaatchi.gmfit.common.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.io.InputStream;

public class PDFViewerActivity extends BaseActivity
    implements OnPageChangeListener, OnLoadCompleteListener {

  @Bind(R.id.pdfView) PDFView pdfView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_pdf);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      String pdfFileName = getIntent().getExtras().getString("PDF_FILE_NAME");

      setupToolbar(getClass().getSimpleName(), toolbar, pdfFileName, true);

      displayFromBytes(Helpers.getInputStreamFromPdfResponseBody());
    }
  }

  private void displayFromBytes(InputStream fileBytes) {
    pdfView.fromStream(fileBytes)
        .defaultPage(0)
        .enableSwipe(true)
        .swipeHorizontal(false)
        .onPageChange(this)
        .enableAnnotationRendering(true)
        .onLoad(this)
        .load();
  }

  @Override public void loadComplete(int i) {

  }

  @Override public void onPageChanged(int i, int i1) {

  }
}
