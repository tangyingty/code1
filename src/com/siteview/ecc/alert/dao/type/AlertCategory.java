package com.siteview.ecc.alert.dao.type;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

/**
 * 告警分类
 * @author hailong.yi
 *
 */
public enum AlertCategory implements TypeInterface{
    Danger,         //危险
    Error,         //错误
    Normal;           //正常

	public String toString(){
		return getStringVaule();
	}
	public static AlertCategory[] getAll(){
        return new AlertCategory[]{
        		Danger,
        		Error,
        		Normal
        		};
	}
	
	public String getImage(){
		if (this == Danger){
			return "/main/images/alert/danger.gif";
		}else if (this == Error){
			return "/main/images/alert/error.gif";
		}else if (this == Normal){
			return "/main/images/alert/normal.gif";
		}
		return "/main/images/alert/none.gif";
	}
	
	public Component getComponent(){
		HboxWithSortValue hbox = new HboxWithSortValue();
		Image alertimage =  new Image(this.getImage());
		alertimage.setAlign("middle");
		Label label = new Label("   " + this.toString());
		alertimage.setParent(hbox);
		label.setParent(hbox);
		hbox.setSortValue(getDisplayString());
		return hbox;
	}
	@Override
	public String getDisplayString() {
		return getStringVaule();
	}
	@Override
	public String getStringVaule() {
		if (this == Danger){
			return Labels.getLabel("Warning");
		}else if (this == Error){
			return Labels.getLabel("Error");
		}else if (this == Normal){
			return Labels.getLabel("Good");
		}
		return Labels.getLabel("Good");
	}
	public static AlertCategory getType(String stringValue) {
		if (Labels.getLabel("Error").equals(stringValue)){
			return AlertCategory.Error;
		}else if (Labels.getLabel("Warning").equals(stringValue)){
			return AlertCategory.Danger;
		}else if (Labels.getLabel("Good").equals(stringValue)){
			return AlertCategory.Normal;
		}
		return AlertCategory.Normal;
	}
	public static AlertCategory getTypeByDisplayString(String displayString) {
		return getType(displayString);
	}
}