package com.flair.bi.service.dto.scheduler;

import java.util.Arrays;

public class ReportLineItem {
	private String visualizationId;
	private String visualizationType;
	private String dimensions[];
	private String measures[];

	public ReportLineItem() {
	}

	public String getVisualizationType() {
		return visualizationType;
	}

	public void setVisualizationType(String visualizationType) {
		this.visualizationType = visualizationType;
	}

	public String[] getDimensions() {
		return dimensions;
	}

	public void setDimensions(String[] dimensions) {
		this.dimensions = dimensions;
	}

	public String[] getMeasures() {
		return measures;
	}

	public void setMeasures(String[] measures) {
		this.measures = measures;
	}

	public String getVisualizationId() {
		return visualizationId;
	}

	public void setVisualizationId(String visualizationId) {
		this.visualizationId = visualizationId;
	}

	@Override
	public String toString() {
		return "ReportLineItem [visualizationId=" + visualizationId + ", visualization=" + visualizationType
				+ ", dimensions=" + Arrays.toString(dimensions) + ", measures=" + Arrays.toString(measures) + "]";
	}

}
