package com.siteview.ecc.dutytable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.siteview.base.manage.View;
import com.siteview.ecc.email.IniFilePack;
import com.siteview.ecc.log.AppendOperateLog;
import com.siteview.ecc.log.OpObjectId;
import com.siteview.ecc.log.OpTypeId;
import com.siteview.ecc.util.Toolkit;

public class AddDutySet extends GenericAutowireComposer {
	
	private static final long 						serialVersionUID = 1L;
	private Include 								eccBody;
	private Textbox 								tableName;
	private Textbox 								description;
	private Combobox 								dutyType;
	private Window                                  addDutySetting;

	public void onInit()throws Exception{
		
	}
	
	
	public void onAddDutySet(Event event) throws Exception{
		String tableNameValue 		= tableName.getValue().trim();
		String descriptionValue 	= description.getValue().trim();
		String dutyTypeValue        = dutyType.getValue().trim();
		
		if("".equals(tableNameValue))
		{
			Messagebox.show(Labels.getLabel("DutyTableNameCannotEmptyString"), "提示", Messagebox.OK, Messagebox.INFORMATION);
			tableName.setFocus(true);
			return;
		}
		if("".equals(descriptionValue)){
			Messagebox.show(Labels.getLabel("DescriptionNotEmptyStrings"), "提示", Messagebox.OK, Messagebox.INFORMATION);
			description.setFocus(true);
			return;
		}
		
		if("".equals(dutyTypeValue)) {
			Messagebox.show(Labels.getLabel("NotChosenDutyType"), "提示", Messagebox.OK, Messagebox.INFORMATION);
			dutyType.setFocus(true);
			return;
		}
		IniFilePack ini = new IniFilePack("watchsheetcfg.ini");
		try{
			ini.load();
		}catch(Exception e){}
			
		Map<String, Map<String, String>> dutyList = new HashMap<String, Map<String, String>>();
		dutyList=ini.getM_fmap();
		Iterator<String> keyIterator = dutyList.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key=(String) keyIterator.next();
			if(tableNameValue.equals(key)){
				Messagebox.show(Labels.getLabel("DutyTableNameExistsPChangeDutyTableName"), "提示", Messagebox.OK, Messagebox.INFORMATION);
				tableName.setFocus(true);
				return;
			}
		}
		try{
			String section = tableNameValue;
			ini.createSection(section);
			ini.setKeyValue(section, "Description", descriptionValue);
			ini.setKeyValue(section, "type",dutyTypeValue);
			ini.setKeyValue(section, "count", "0");
			ini.saveChange();
			
			Session session=this.session;
			session.setAttribute(DutyConstant.Add_DutySet_Section, section);//设置关键值的时候 要绑定 state 值
			session.setAttribute(DutyConstant.State, "1");
			
			View view = Toolkit.getToolkit().getSvdbView(event.getTarget().getDesktop());
			String loginname = view.getLoginName();
			String minfo=loginname+" "+Labels.getLabel("In")+OpObjectId.duty_set.name+Labels.getLabel("Conducting")+OpTypeId.add.name+Labels.getLabel("OperationAdd")+tableName.getValue();
			AppendOperateLog.addOneLog(loginname, minfo, OpTypeId.add, OpObjectId.duty_set);	
			
			onRefresh();
		}catch(Exception e){
			Messagebox.show(e.getMessage(),"错误", Messagebox.OK, Messagebox.ERROR);
		}

	}
	
	public void onRefresh()throws Exception{
		try{
			String targetUrl = "/main/setting/setmaintain.zul";
			eccBody = (Include) (this.desktop.getPage("eccmain").getFellow("eccBody"));
			eccBody.setSrc(null);
			eccBody.setSrc(targetUrl);
		}catch(Exception e){
			Messagebox.show(e.getMessage(),"错误", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
}
