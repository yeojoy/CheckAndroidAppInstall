package me.yeojoy.checksomething;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView mTvContents;
	private PackageManager mPm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTvContents = (TextView) findViewById(R.id.tv_contents);
		mPm = getPackageManager();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setAppInstallerInfo();
		
	}
	
	private void setAppInstallerInfo() {
		
		int flags = PackageManager.GET_SIGNATURES;

		String packageName = null;
        PackageInfo packageInfo = null;

        Signature[] signatures = null;

        CertificateFactory cf = null;
		
		
		List<ApplicationInfo> appInfoList = mPm.getInstalledApplications(0);
		
		StringBuilder sb = new StringBuilder();
		
		for (ApplicationInfo appInfo : appInfoList) {
			
			packageName = appInfo.packageName;
			try {
				packageInfo = mPm.getPackageInfo(packageName, flags);
			} catch (NameNotFoundException e) {
				// TODO some error checking
				e.printStackTrace();
			}
			
			signatures = packageInfo.signatures;
			// cert = DER encoded X.509 certificate:
			byte[] cert = signatures[0].toByteArray();
			InputStream input = new ByteArrayInputStream(cert);
			
			try {
				cf = CertificateFactory.getInstance("X509");
			} catch (CertificateException e) {
				// TODO some error checking
				e.printStackTrace();
			}
			X509Certificate x509Certificate = null;
			try {
				x509Certificate = (X509Certificate) cf.generateCertificate(input);
			} catch (CertificateException e) {
				// TODO some error checking
				e.printStackTrace();
			}
			
			sb.append("App name : ").append(mPm.getApplicationLabel(appInfo)).append("\n");
			sb.append("Package Name : ").append(packageName).append("\n");
			sb.append("Installer     : ").append(mPm.getInstallerPackageName(packageName)).append("\n");
			sb.append("Certificate for: ").append(x509Certificate.getSubjectDN()).append("\n");
			sb.append("Certificate issued by: ").append(x509Certificate.getIssuerDN()).append("\n");
			sb.append("The certificate is valid from ").append(x509Certificate.getNotBefore());
			sb.append(" to ").append(x509Certificate.getNotAfter()).append("\n");
			sb.append("Certificate SN# ").append(x509Certificate.getSerialNumber()).append("\n");
			sb.append("Generated with ").append(x509Certificate.getSigAlgName()).append("\n");
			sb.append("\n\n");
		}
		
		
		mTvContents.setText(sb);
		
	}

}
