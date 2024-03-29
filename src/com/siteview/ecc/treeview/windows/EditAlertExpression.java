package com.siteview.ecc.treeview.windows;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.siteview.base.template.MonitorTemplate;

public class EditAlertExpression extends GenericForwardComposer
{
	private final static Logger logger = Logger.getLogger(EditAlertExpression.class);
	Window					editalertexpression;
	Combobox				expr;
	Combobox				fh;
	Textbox					v1;
	
	Radio					rdy;
	Radio					rdh;
	
	Button					btnadd;
	Listbox					lb1;
	Button					btndel;
	Button					btnok;
	Button					btnclose;
	
	Comboitem				item;
	Listitem				listitem;
	Listcell				listcell;
	
	Textbox					tb;
	MonitorTemplate			monitorTemplate;
	Map<String, String>	exprs;
	
	public EditAlertExpression()
	{
		
	}
	
	public void onCreate$editalertexpression()
	{
		expr.setReadonly(true);
		fh.setReadonly(true);
		buildcb();
		//
		tb = (Textbox) editalertexpression.getAttribute("tb");
		monitorTemplate = (MonitorTemplate) editalertexpression.getAttribute("monitorTemplate");
		buildbdcb(monitorTemplate.get_Return_Items());
		//
		exprs = (Map<String, String>) tb.getAttribute("expr");
		buildgrid(exprs, monitorTemplate.get_Return_Items());
	}
	
	public void onClick$btndel()
	{
		int count=lb1.getItemCount();
		while(true)
		{
			int i=lb1.getSelectedIndex();
			if(i==-1)
			{
				break;
			}
			lb1.removeItemAt(i);
		}
//		for (int i = 0; i < count; i++)
//		{
//			Listitem item = lb1.getItemAtIndex(i);
//			if (item.isSelected())
//			{
//				lb1.removeItemAt(i);
//			}
//		}
	}
	
	public void onClick$btnok()
	{
		Map<String, String> expr = new HashMap<String, String>();
		TreeSet<String> relations = null;
		for (int i = 1; i < lb1.getItemCount() + 1; i++)
		{
			Listitem item = lb1.getItemAtIndex(i - 1);
			List list = item.getChildren();
			String name = (String) item.getValue();
			String operate = ((Listcell) list.get(1)).getLabel();
			String value = ((Listcell) list.get(2)).getLabel();
			String relation = null;
			try
			{
				relation = ((Listcell) list.get(3)).getLabel();
			} catch (Exception ex)
			{
				
			}			
			name=name.trim();
			operate=operate.trim();
			value=value.trim();
			
			expr.put("sv_paramname" + i, name);
			expr.put("sv_operate" + i, operate);
			expr.put("sv_paramvalue" + i, value);
			if (relation != null && i != lb1.getItemCount())
			{
				relation = relation.equals("与") ? "and" : "or";
				expr.put("sv_relation" + i, relation);
				relations = new TreeSet<String>();
				relations.add(relation);
			}
			
		}
		expr.put("sv_conditioncount", "" + lb1.getItemCount());
		String express = "1";
		int i = 1;
		if (relations != null)
		{
			for (String key : relations)
			{
				express = express + "#" + key + "#"+(i + 1);
			}
		}
		expr.put("sv_expression", express);
		tb.setAttribute("expr", expr);
		String expression = AddMonitor.buildExpression(expr, monitorTemplate.get_Return_Items());
		logger.info("***************************************************"+relations);
		tb.setValue(expression);
		editalertexpression.detach();
	}
	
	public void onClick$btnadd()
	{
		
		String paramvalue = v1.getValue();
		try
		{
			if (expr.getSelectedItem() == null)
			{
				Messagebox.show("项目表达式不能为空！", "提示", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			if (paramvalue.isEmpty())
			{
				Messagebox.show("项目表达式的值不能为空！", "提示", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			if (fh.getSelectedItem() == null)
			{
				
				Messagebox.show("操作符不能为空！", "提示", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			
		} catch (Exception e)
		{
		}
		listitem = new Listitem();
		listitem.setParent(lb1);
		listitem.setValue(expr.getSelectedItem().getValue());
		String label = expr.getSelectedItem().getLabel();
		
		String operate = fh.getSelectedItem().getLabel();
		
		String sv_relation = "";
		if (rdy.isChecked())
		{
			sv_relation = "与";
		} else
		{
			sv_relation = "或";
		}
		listcell = new Listcell(label);
		listcell.setParent(listitem);
		listcell = new Listcell(operate);
		listcell.setParent(listitem);
		listcell = new Listcell(paramvalue);
		listcell.setParent(listitem);
		
		if (lb1.getItemCount() > 1)
		{
			Listitem lastitem = lb1.getItemAtIndex(lb1.getItemCount() - 2);
			listcell = new Listcell(sv_relation);
			listcell.setParent(lastitem);
		}
		
	}
	
	private void buildbdcb(List<Map<String, String>> returnvalues)
	{
		for (Map<String, String> item1 : returnvalues)
		{
			String name = item1.get("sv_name");
			String lb = item1.get("sv_label");
			item = new Comboitem();
			item.setValue(name.trim());
			item.setLabel(lb.trim());
			expr.appendChild(item);
		}
	}
	
	private void buildgrid(Map<String, String> expr, List<Map<String, String>> returnvalues)
	{
		if (expr == null)
		{
			return;
		}
		if (returnvalues == null)
		{
			return;
		}
		Map<String, String> itemMapping = new LinkedHashMap<String, String>();
		for (Map<String, String> hashMap : returnvalues)
		{
			String itemName = hashMap.get("sv_name");
			String itemLabel = hashMap.get("sv_label");
			if (itemName == null || itemName.isEmpty())
			{
				continue;
			}
			itemMapping.put(itemName.trim(), itemLabel.trim());
		}
		String label = "";
		StringBuilder AlertExpression = new StringBuilder();
		int condtionCount = Integer.parseInt(expr.get("sv_conditioncount"));
		for (int i = 1; i < condtionCount + 1; i++)
		{
			String item = expr.get("sv_paramname" + i);
			if (!itemMapping.containsKey(item))
			{
				continue;
			}
			listitem = new Listitem();
			listitem.setValue(item);
			listitem.setParent(lb1);
			label = itemMapping.get(item);
			String paramvalue = expr.get("sv_paramvalue" + i);
			String operate = " " + expr.get("sv_operate" + i);
			String sv_relation = expr.get("sv_relation" + i);
			if (sv_relation != null)
			{
				sv_relation = sv_relation.equals("or") ? "或 " : "与 ";
			}
			listcell = new Listcell(label);
			listcell.setParent(listitem);
			listcell = new Listcell(operate);
			listcell.setParent(listitem);
			listcell = new Listcell(paramvalue);
			listcell.setParent(listitem);
			
			if (sv_relation != null)
			{
				listcell = new Listcell(sv_relation);
				listcell.setParent(listitem);
			}
		}
	}
	
	private void buildcb()
	{
		// 符号
		item = new Comboitem();
		item.setValue("==");
		item.setLabel("==");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue("!=");
		item.setLabel("!=");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue(">=");
		item.setLabel(">=");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue(">");
		item.setLabel(">");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue("<=");
		item.setLabel("<=");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue("<");
		item.setLabel("<");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue("contains");
		item.setLabel("contains");
		fh.appendChild(item);
		item = new Comboitem();
		item.setValue("!contains");
		item.setLabel("!contains");
		fh.appendChild(item);
		//
	}
}
