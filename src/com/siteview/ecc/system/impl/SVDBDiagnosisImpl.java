package com.siteview.ecc.system.impl;

import org.zkoss.util.resource.Labels;

import com.siteview.ecc.system.Diagnosis;
import com.siteview.svdb.UnivData;

public class SVDBDiagnosisImpl extends Diagnosis {
	@Override
	public String getDescription() {
		return Labels.getLabel("SvdbDetectionInfo");
	}

	@Override
	public String getName() {
		return Labels.getLabel("SvdbDetection");
	}

	@Override
	public void execute() throws Exception {
		String ip = UnivData.getSvdbAddr();
		getResultList().add("svdb server : " + ip);
	}

}
