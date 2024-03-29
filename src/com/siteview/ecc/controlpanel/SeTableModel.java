package com.siteview.ecc.controlpanel;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;

import com.siteview.base.manage.View;
import com.siteview.base.tree.INode;
import com.siteview.base.treeInfo.SeInfo;
import com.siteview.ecc.timer.NodeInfoBean;
import com.siteview.ecc.treeview.EccTreeItem;
import com.siteview.ecc.util.Toolkit;

public class SeTableModel extends EccListModel {
	@Override
	public int getColCount() {
		return 6;
	}
	@Override
	public String getTitle(int idxCol) {
		switch(idxCol)
		{
			case 0:
				return "名称";
			case 1:
				return "设备总数";
			case 2:
				return "监测器总数";
			case 3:
				return "其中被禁止";
			case 4:
				return "错误";
			case 5:
				return "危险";
		}
		return "";
	}
	@Override
	public int forceColWidth(int idxCol) {
		
		switch(idxCol)
		{
		case 1:
			return 90;
		case 2:
			return 110;
		case 3:
			return 110;
		case 4:
			return 70;
		case 5:
			return 70;
		}
		return -1;
	}
	@Override
	public boolean isNumber(int idxCol) {
		
		switch(idxCol)
		{
			case 1:
				return true;
			case 2:
				return true;
			case 3:
				return true;
			case 4:
				return true;
			case 5:
				return true;
		}
		return false;
	}

	@Override
	public ListDataBean getValue(Object rowValue) 
	{
		ListDataBean bean = new ListDataBean();
		SeInfo node=(SeInfo)Toolkit.getToolkit().getInfoObject(view, ((EccTreeItem)rowValue).getValue());
		if(node==null)
			return null;
		//使用虚拟视图时刷新，刷新节点监测器信息
		Toolkits tool = new Toolkits();
		NodeInfoBean nodeBean = tool.refreshNodeInfoInList((EccTreeItem)rowValue);
		
		bean.setLineNum(6);
		bean.setName(node.getName());
		bean.setEntitySum(nodeBean.getDevice() + "");
		bean.setMonitorSum(nodeBean.getAll() + "");
		bean.setMonitorDisableSum(nodeBean.getDisabled() + "");
		bean.setMonitorErrorSum(nodeBean.getError() + "");
		bean.setMonitorWarningSum(String.valueOf(nodeBean.getWarning() + ""));
		
//		switch(idxCol)
//		{
//			case 0:
//				return node.getName();
//			case 1:
//				return String.valueOf(node.get_sub_entity_sum(session));
//			case 2:
//				return String.valueOf(node.get_sub_monitor_sum(session));
//			case 3:
//				return String.valueOf(node.get_sub_monitor_disable_sum(session));
//			case 4:
//				return String.valueOf(node.get_sub_monitor_error_sum(session));
//			case 5:
//				return String.valueOf(node.get_sub_monitor_warning_sum(session));
//		}
		return bean;
	}
	private static final long serialVersionUID = 4607324331066529184L;
	public SeTableModel(View view, EccTreeItem selectedNode) {
		super(view, selectedNode);
	}
	public SeTableModel(View view, EccTreeItem selectedNode,boolean displayInherit) {
		super(view, selectedNode,displayInherit);
	}
	public SeTableModel(View view, EccTreeItem selectedNode,boolean displayInherit,int filter) {
		super(view, selectedNode,displayInherit,filter);
	}
	public void refresh()
	{
		ArrayList<EccTreeItem> list=new ArrayList<EccTreeItem>(); 
		addInherit(parentNode,list);
		clear();
		addAll(list);
	}
	private void addInherit(EccTreeItem item,List<EccTreeItem> list)
	{
		if(item!=null)
		for(EccTreeItem child:item.getChildRen())
		{
			if(child.getType().equals(INode.SE))
				super.addByFilter(list,child);
			else if(isInherit())
		  		addInherit(child,list);
		}
	}


}
