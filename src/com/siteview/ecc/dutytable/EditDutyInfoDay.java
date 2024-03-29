package com.siteview.ecc.dutytable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Longbox;

import com.siteview.base.manage.View;
import com.siteview.ecc.email.IniFilePack;
import com.siteview.ecc.log.AppendOperateLog;
import com.siteview.ecc.log.OpObjectId;
import com.siteview.ecc.log.OpTypeId;
import com.siteview.ecc.util.Toolkit;

public class EditDutyInfoDay extends GenericAutowireComposer {
	
	private Textbox 							oldTableName;
	private Textbox 							newTableName;
	private Textbox 							oldDescription;
	private Textbox 							newDescription;
	private Combobox 							dutyType;
	private Textbox 							alarmEmailbox;
	private Textbox 							mobilePhoneNum;
	private Timebox 							beginduty;
	private Timebox 							endduty;	
	private Include 							eccBody;
	private Window 								editDutyInfoSetting;
	
	private String 								edit_dutyfather_section = "";
	private String                              edit_dutySon_section = "";
	
	public void onInit() throws Exception{
		this.edit_dutyfather_section = (String)editDutyInfoSetting.getAttribute(DutyConstant.Edit_DutyFather_Section);
		this.edit_dutySon_section    = (String)editDutyInfoSetting.getAttribute(DutyConstant.Edit_DutySon_Section);
		
		IniFilePack ini = new IniFilePack("watchsheetcfg.ini");
		try{
			ini.load();
		}catch(Exception e){e.printStackTrace();}
		
		String item1 = ini.getM_fmap().get(edit_dutyfather_section).get(edit_dutySon_section);
		String[] value = new String[6];
		value = item1.split(",");

		mobilePhoneNum.setValue(value[4]);//解析   String -- long
		alarmEmailbox.setValue(value[5]);
		SimpleDateFormat smf1 = new SimpleDateFormat("yyyy-MM-dd");
		try{
			SimpleDateFormat df3 = new SimpleDateFormat("HH:mm");
			beginduty.setValue(df3.parse(value[0] + ":" + value[1]));
			endduty.setValue(df3.parse(value[2] + ":" + value[3]));
		}catch(Exception e){e.printStackTrace();}
	}	
	
	public void onReFresh(Event event) {
		try{
			session.setAttribute(DutyConstant.Edit_DutyFather_Section, edit_dutyfather_section);
			session.setAttribute(DutyConstant.Edit_DutySon_Section, edit_dutySon_section);
			session.setAttribute(DutyConstant.State, "4");//绑定状态值
			
			editDutyInfoSetting.detach();
			String targetUrl = "/main/setting/setmaintain.zul";
			eccBody = (Include) (this.desktop.getPage("eccmain").getFellow("eccBody"));
			eccBody.setSrc(null);
			eccBody.setSrc(targetUrl);
		}catch(Exception e){
			e.getStackTrace();
		}
	}	
	
	
	public void onSaveDutyInfo(Event event)throws Exception{
		try{
			String section	= this.edit_dutyfather_section;
			String itemX	= this.edit_dutySon_section;
			
			String mobileValue 	= 	mobilePhoneNum.getValue().toString();			
			if ("".endsWith(mobileValue.trim())) {
				try{
					Messagebox.show(Labels.getLabel("MobilePhoneNumberCanNotEmpty"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				}catch(Exception e){}
				mobilePhoneNum.setValue(null);
				mobilePhoneNum.setFocus(true);
				return;
			}
			long mobileLong = 0;
			try{
				mobileLong = Long.parseLong(mobileValue);
				if(mobileLong > Long.parseLong("19999999999") ||
						mobileLong < Long.parseLong("10000000000")){//11位
					throw new Exception("");
				}
			}catch(Exception e){
				e.printStackTrace();
				Messagebox.show(Labels.getLabel("MobilePhoneNumberNotCorrect"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				mobilePhoneNum.setFocus(true);
				return;
			}
			
			if(alarmEmailbox.getValue()== null ||alarmEmailbox.getValue().trim().isEmpty() || alarmEmailbox.getValue().trim().equals(""))
			{
				Messagebox.show(Labels.getLabel("DetailedInformationAlarmReceivingMailboxCannotEmpty"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				alarmEmailbox.setFocus(true);
				return;
			}
			
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(alarmEmailbox.getValue());
			boolean isMatched = matcher.matches();
			if(!isMatched){
				Messagebox.show(Labels.getLabel("MessageNotCorrectFormat"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				alarmEmailbox.setValue("");
				alarmEmailbox.setFocus(true);
				return;
			}
			if (beginduty.getValue() == null||"".equals(beginduty.getValue())) {
				Messagebox.show(Labels.getLabel("NotChosenStartTime"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				beginduty.setFocus(true);
				return;
			}
			if (endduty.getValue() == null||"".equals(endduty.getValue())) {
				Messagebox.show(Labels.getLabel("NotChosenEndTime"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				endduty.setFocus(true);
				return;
			}
			String item1Value = "";		
			String[] time1 = new String[2];
			SimpleDateFormat df3 = new SimpleDateFormat("HH:mm");
			time1 = df3.format(beginduty.getValue()).split(":");
			String beginTime="";
			for (int i = 0; i < time1.length; i++) {
				item1Value = item1Value + time1[i] + ",";
				beginTime=beginTime+time1[i];
			}
		
			String[] time2 = new String[2];
			time2 = df3.format(endduty.getValue()).split(":");
			String endTime="";
			for (int i = 0; i < time2.length; i++) {
				item1Value = item1Value + time2[i] + ",";
				endTime=endTime+time2[i];
			}
		
			if(Integer.parseInt(beginTime)>Integer.parseInt(endTime)){
				Messagebox.show(Labels.getLabel("StartTimeGreaterThanOrEqualEndTimeWrongChoice"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				beginduty.setValue(null);
				beginduty.setFocus(true);
				return;
			}
			
			item1Value = item1Value + mobilePhoneNum.getValue() + ","+ alarmEmailbox.getValue().trim();
			IniFilePack ini = new IniFilePack("watchsheetcfg.ini");
			try{
				ini.load();
			}catch(Exception e){}

			ini.setKeyValue(section, itemX, item1Value);
			ini.saveChange();
			View view = Toolkit.getToolkit().getSvdbView(event.getTarget().getDesktop());
			String loginname = view.getLoginName();
			String minfo=loginname+" "+Labels.getLabel("In")+OpObjectId.duty_set.name+Labels.getLabel("Conducting")+OpTypeId.edit.name+Labels.getLabel("DetailedInformationOfOperation");
			AppendOperateLog.addOneLog(loginname, minfo, OpTypeId.edit, OpObjectId.duty_set);
			Session session = this.session;
			
			session.setAttribute(DutyConstant.Edit_DutyFather_Section, section);
			session.setAttribute(DutyConstant.Edit_DutySon_Section, itemX);
			session.setAttribute(DutyConstant.State, "4");//绑定状态值
			
			editDutyInfoSetting.detach();
			String targetUrl = "/main/setting/setmaintain.zul";
			eccBody = (Include) (this.desktop.getPage("eccmain").getFellow("eccBody"));
			eccBody.setSrc(null);
			eccBody.setSrc(targetUrl);
			
		}catch(Exception e){
			e.printStackTrace();
			Messagebox.show(e.getMessage(),"错误", Messagebox.OK, Messagebox.ERROR);
		}
	}
}
