package com.afunms.topology.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.application.dao.NetworkDao;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.PanelModelDao;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.config.model.PanelModel;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.NodeCategory;
import com.afunms.polling.node.Host;

@SuppressWarnings("unchecked")
public class NodeHelper {
	private static HashMap categoryMap;

	private static HashMap categoryNameMap;

	private static List categoryList;
	static {
		categoryNameMap = new HashMap();
		categoryMap = new HashMap();
		categoryList = new ArrayList();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/node-category.xml"));
			List list = doc.getRootElement().getChildren("category");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element ele = (Element) it.next();
				NodeCategory category = new NodeCategory();
				String id = ele.getAttributeValue("id");
				category.setId(Integer.parseInt(id));
				category.setCnName(ele.getChildText("cn_name"));
				category.setEnName(ele.getChildText("en_name"));
				category.setTopoImage("image/topo/" + ele.getChildText("topo_image"));
				category.setAlarmImage("image/topo/" + ele.getChildText("alarm_image"));
				category.setAlarmImage_1("image/topo/" + ele.getChildText("alarm_image").replace(".gif", "-1.gif"));
				category.setLostImage("image/topo/" + ele.getChildText("lost_image"));
				categoryMap.put(id, category);
				categoryNameMap.put(category.getEnName(), category);
				categoryList.add(category);
			}
		} catch (Exception e) {
			SysLogger.error("NodeHelper.static", e);
		}
	}

	/**
	 * 节点在拓扑图上的报警时的图标
	 */
	public static String getAlarmImage(int category) {
		return getCategory(category).getAlarmImage();
	}

	/**
	 * 节点在拓扑图上的报警时的图标
	 */
	public static String getAlarmImage(String category) {
		return getCategory(category).getAlarmImage();
	}

	public static String getAlarmImage1(String category) {
		return getCategory(category).getAlarmImage_1();
	}

	/**
	 * 报警级别描述
	 */
	public static String getAlarmLevelDescr(int level) {
		String descr = null;
		if (level == 1) {
			descr = "注意";
		} else if (level == 2) {
			descr = "故障";
		} else if (level == 3) {
			descr = "严重";
		}
		return descr;
	}

	/**
	 * 报警级别标志
	 */
	public static String getAlarmLevelImage(int level) {
		String image = null;
		if (level == 1) {
			image = "alarm_level_1.gif";
		} else if (level == 2) {
			image = "alarm_level_2.gif";
		} else if (level == 3) {
			image = "alarm_level_3.gif";
		}
		return "image/topo/" + image;
	}

	public static List getAllCategorys() {
		return categoryList;
	}

	private static NodeCategory getCategory(int id) {
		if (categoryMap.get(String.valueOf(id)) != null) {
			return (NodeCategory) categoryMap.get(String.valueOf(id));
		} else {
			return (NodeCategory) categoryMap.get("1000");
		}
	}

	private static NodeCategory getCategory(String category) {
		if (categoryNameMap.get(category) != null) {
			return (NodeCategory) categoryNameMap.get(category);
		} else {
			System.out.println("category is not exist,categoryName=" + category);
			return (NodeCategory) categoryNameMap.get("1000");
		}
	}

	/**
	 * 节点状态标志
	 */
	public static String getCurrentStatusImage(int status) {
		String image = null;
		if (status == 0) {
			image = "status_ok.gif";
		} else if (status == 1) {
			image = "alarm_level_1.gif";
		} else if (status == 2) {
			image = "alarm_level_2.gif";
		} else if (status == 3) {
			image = "alert.gif";
		} else {
			image = "unmanaged.gif";
		}
		return "image/topo/" + image;
	}

	// 主机实体设备右键菜单 yangjun add
	public static String getHostMenu(String nodeId, String ip, String category) {
		String menuItem = "<a class=\"ping_menu_out\" onmouseover=\"pingMenuOver();\" onmouseout=\"pingMenuOut();\""
				+ " onclick=\"javascript:resetProcDlg();window.showModelessDialog('/afunms/tool/ping.jsp?ipaddress="
				+ ip
				+ "',"
				+ " window, 'dialogHeight:500px;dialogWidth:500px;status:0;help:0;edge:sunken;center:yes;scroll:0');"
				+ "timingCloseProDlg(8000);\" title=\"ping "
				+ ip
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;Ping </a><br/>"
				+

				// <img src="/afunms/resource/image/toolbar/telnet.gif">&nbsp;<a
				// href="javascript:void(null)"
				// onClick='window.open("/afunms/network.do?action=telnet&ipaddress=172.25.25.254","onetelnet",
				// "height=0, width= 0, top=0, left= 0")'>Telnet</a>

				"<a class=\"trace_menu_out\" onmouseover=\"traceMenuOver();\" onmouseout=\"traceMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/tool/tracerouter.jsp?ipaddress="
				+ ip
				+ "','window', "
				+ "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;Traceroute</a><br/>"
				+

				"<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" "
				+ "onclick=\"showalert('"
				+ nodeId
				+ "')"
				+ "\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>"
				+

				"<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=0','window', "
				+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;生成别名拓扑图 </a><br/>"
				+

				"<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=1','window', "
				+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;生成IP拓扑图 </a><br/>";

		return menuItem;
	}

	public static String getHostOS(String sysOid) {
		String os = null;
		if (sysOid.startsWith("1.3.6.1.4.1.311.")) {
			os = "windows";
		} else if (sysOid.equals("1.3.6.1.4.1.2021.250.10") || sysOid.equals("1.3.6.1.4.1.8072.3.2.10")) {
			os = "linux";
		} else if (sysOid.startsWith("1.3.6.1.4.1.42.")) {
			os = "solaris";
		} else if (sysOid.startsWith("1.3.6.1.4.1.2.")) {
			os = "aix";
		} else if (sysOid.startsWith("1.3.6.1.4.1.36.")) {
			os = "tru64";
		} else if (sysOid.startsWith("1.3.6.1.4.1.9.")) {
			os = "cisco";
		} else {
			os = "";
		}
		return os;
	}

	// 墙面信息点右键菜单
	public static String getInfoMenuItem(String nodeId) {
		String menuItem = "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" " + "onclick=\"showInfo('" + nodeId + "')" + "\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;查看信息</a><br/>";

		return menuItem;

	}

	/**
	 * 墙面信息点状态标志
	 */
	public static String getInfoStatusImage(int status) {
		String image = null;
		if (status == 2) {
			image = "info_green.gif";
		} else if (status == 1) {
			image = "info_blue.gif";
		} else if (status == 3) {
			image = "info_red.gif";
		} else {
			image = "info_yellow.gif";
		}
		return "image/topo/" + image;
	}

	/**
	 * 节点在拓扑图上的报警时的图标
	 */
	public static String getLostImage(int category) {
		return getCategory(category).getLostImage();
	}

	// 网络实体设备右键菜单
	public static String getMenu(String nodeId, String ip, String category) {
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeId.substring(3)));
		String sysoid = "";
		String width = "";
		String type = "";
		String subtype = "";
		int height = 0;
		if (host != null) {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
			type = nodedto.getType();
			subtype = nodedto.getSubtype();
			sysoid = host.getSysOid();
			IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao();
			IpaddressPanel ipaddressPanel = null;
			try {
				ipaddressPanel = ipaddressPanelDao.loadIpaddressPanel(ip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ipaddressPanelDao.close();
			}
			if (ipaddressPanel != null) {
				PanelModelDao panelModelDao = new PanelModelDao();
				PanelModel panelModel = null;
				try {
					panelModel = (PanelModel) panelModelDao.loadPanelModel(sysoid, ipaddressPanel.getImageType());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					panelModelDao.close();
				}
				if (panelModel != null) {
					width = panelModel.getWidth();
					height = Integer.parseInt(panelModel.getHeight());
				}
			}
		}

		String menuItem = "";
		if (host != null && host.getCategory() == 4) {
			menuItem = "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials3"
					+ nodeId
					+ "')\" onmouseout=\"hidemenu('tutorials3"
					+ nodeId
					+ "')\">"
					+ "<a class=\"detail_mainmenu_out\" onmouseover=\"detailMainMenuOver();\" onmouseout=\"detailMainMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信息查看&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials3"
					+ nodeId
					+ "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/detail/dispatcher.jsp?id="
					+ nodeId
					+ "','window', "
					+ "'toolbar=no,height=670,width=1024,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"sbmb_menu_out\" onmouseover=\"sbmbMenuOver();\" onmouseout=\"sbmbMenuOut();\" "
					+ "onclick=\"showpanel('"
					+ ip
					+ "','"
					+ width
					+ "','"
					+ height
					+ "')"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备面板 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=0','window', "
					+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;生成别名拓扑图 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=1','window', "
					+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;生成IP拓扑图 </a><br/>"
					+ "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials4"
					+ nodeId
					+ "')\" onmouseout=\"hidemenu('tutorials4"
					+ nodeId
					+ "')\">"
					+ "<a class=\"set_menu_out\" onmouseover=\"setMenuOver();\" onmouseout=\"setMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;资源设置&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials4"
					+ nodeId
					+ "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('"
					+ "/afunms/hostApply.do?action=list&nodeid="
					+ nodeId
					+ "&ipaddress="
					+ ip
					+ "',portScanWindow,"
					+ "'toolbar=no,height=500, width= 1000, top=100, left=100,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes');"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应用配置</a><br/>"
					+ "</td></tr>"
					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('"
					+ "/afunms/processgroup.do?action=list&nodeid="
					+ nodeId
					+ "&ipaddress="
					+ ip
					+ "',window,"
					+ "'toolbar=no,height=500, width= 1000, top=100, left=100,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes');"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;进程监视配置</a><br/>"
					+ "</td></tr>"
					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('"
					+ "/afunms/hostservicegroup.do?action=list&nodeid="
					+ nodeId
					+ "&ipaddress="
					+ ip
					+ "',window,"
					+ "'toolbar=no,height=500, width= 1000, top=100, left=100,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes');"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;服务监视配置</a><br/>"
					+ "</td></tr>"
					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('"
					+ "/afunms/disk.do?action=toolbarlist&nodeid="
					+ nodeId
					+ "&ipaddress="
					+ ip
					+ "',window,"
					+ "'toolbar=no,height=500, width= 1000, top=100, left=100,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes');"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;磁盘阈值</a><br/>"
					+ "</td></tr>"
					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('"
					+ "/afunms/nodesyslogrule.do?action=toolbarfilter&nodeid="
					+ nodeId
					+ "&ipaddress="
					+ ip
					+ "',window,"
					+ "'toolbar=no,height=500, width= 1000, top=100, left=100,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes');"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Syslog告警配置</a><br/>"
					+ "</td></tr>"
					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('" + "/afunms/hostApply.do?action=list&nodeid=" + nodeId + "&ipaddress=" + ip + "',window,"
					+ "'toolbar=no,height=500, width= 1000, top=100, left=100,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes')" + "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备相关应用</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=inporthost&type=" + category + "&nodeId=" + nodeId + "',"
					+ " window, 'dialogwidth:600px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;导入服务器 </a><br/>"
					+ "</td></tr>" + "<tr><td class=\"menu\">" + "<a class=\"collection_menu_out\" onmouseover=\"collectionMenuOver();\" onmouseout=\"collectionMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/nodeGatherIndicators.do?action=list&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;采集指标 </a><br/>" + "</td></tr>" + "<tr><td class=\"menu\">"
					+ "<a class=\"threshold_menu_out\" onmouseover=\"thresholdMenuOver();\" onmouseout=\"thresholdMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/alarmIndicatorsNode.do?action=list&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "&ipaddress=" + ip + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;指标阀值 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"port_menu_out\" onmouseover=\"portMenuOver();\" onmouseout=\"portMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/portconfig.do?action=nodeportlist&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "&ipaddress=" + ip + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口配置 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"portthreshold_menu_out\" onmouseover=\"portthresholdMenuOver();\" onmouseout=\"portthresholdMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/alarmport.do?action=list&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype=" + subtype
					+ "&ipaddress=" + ip + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口阀值 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"portthreshold_menu_out\" onmouseover=\"portthresholdMenuOver();\" onmouseout=\"portthresholdMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/alarmIndicatorsNode.do?action=showtosetshowkeylist&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "&ipaddress=" + ip + "','window', " + "'toolbar=no,height=600,width=600,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;快捷指标 </a><br/>" + "</td></tr>"

					+ "</table></td><tr></table>" 
					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials1" + nodeId
					+ "')\" onmouseout=\"hidemenu('tutorials1" + nodeId + "')\">"
					+ "<a class=\"manage_mainmenu_out\" onmouseover=\"manageMainMenuOver();\" onmouseout=\"manageMainMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备管理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials1" + nodeId + "\" width=\"135\" border=\"0\">" + "<tr><td class=\"menu\">"
					+ "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=equipProperty&type=" + category + "&nodeId=" + nodeId + "',"
					+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;图元属性 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"relationmap_menu_out\" onmouseover=\"relationMapMenuOver();\" onmouseout=\"relationMapMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=relationList&nodeId=" + nodeId + "&category=" + category + "',"
					+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;关联拓扑图 </a><br/>"
					+ "</td></tr>" + "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" "
					+ "onclick=\"deleteEquip('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统删除设备</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" "
					+ "onclick=\"removeEquip('" + nodeId + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;拓扑图删除设备</a><br/>" + "</td></tr>" + "<tr><td class=\"menu\">"
					+ "<a class=\"portscan_menu_out\" onmouseover=\"portscanMenuOver();\" onmouseout=\"portscanMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/ipDistrictMatch.do?action=savePortScan&refresh=refresh&ipaddress=" + ip + "',"
					+ " window, 'dialogwidth:600px; dialogheight:400px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口扫描 </a><br/>"
					+ "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials2" + nodeId + "')\" onmouseout=\"hidemenu('tutorials2" + nodeId + "')\">"
					+ "<a class=\"tool_menu_out\" onmouseover=\"toolMenuOver();\" onmouseout=\"toolMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;工&nbsp;&nbsp;&nbsp;&nbsp;具&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials2" + nodeId + "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\"><a class=\"ping_menu_out\" onmouseover=\"pingMenuOver();\" onmouseout=\"pingMenuOut();\""
					+ " onclick=\"javascript:resetProcDlg();window.showModelessDialog('/afunms/tool/ping.jsp?ipaddress=" + ip + "',"
					+ " window, 'dialogHeight:500px;dialogWidth:500px;status:0;help:0;edge:sunken;center:yes;scroll:0');" + "timingCloseProDlg(8000);\" title=\"ping " + ip
					+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ping </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"telnet_menu_out\" onmouseover=\"telnetMenuOver();\" onmouseout=\"telnetMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/network.do?action=telnet&&ipaddress=" + ip + "','window', "
					+ "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Telnet</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"trace_menu_out\" onmouseover=\"traceMenuOver();\" onmouseout=\"traceMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/tool/tracerouter.jsp?ipaddress=" + ip + "','window', "
					+ "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Traceroute</a><br/>" + "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials5" + nodeId + "')\" onmouseout=\"hidemenu('tutorials5" + nodeId + "')\">"
					+ "<a class=\"alarm_menu_out\" onmouseover=\"alarmMenuOver();\" onmouseout=\"alarmMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials5" + nodeId + "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\"><a class=\"confirmAlarm_menu_out\" onmouseover=\"confirmAlarmMenuOver();\" onmouseout=\"confirmAlarmMenuOut();\" "
					+ "onclick=\"confirmAlarm('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警确认</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/monitor.do?action=hosteventlist&nodetype=" + category.substring(0, 3) + "&id=" + nodeId.substring(3)
					+ "','window', " + "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警列表</a><br/>"
					+ "</td></tr>" + "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials6" + nodeId + "')\" onmouseout=\"hidemenu('tutorials6" + nodeId + "')\">"
					+ "<a class=\"report_mainmenu_out\" onmouseover=\"reportMainMenuOver();\" onmouseout=\"reportMainMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials6" + nodeId + "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\"><a class=\"report_menu_out\" onmouseover=\"reportMenuOver();\" onmouseout=\"reportMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/netreport.do?action=showPingReport&ipaddress=" + ip + "','window', "
					+ "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表查看</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/hostreport.do?action=serverReport&nodetype=" + category.substring(0, 3) + "&id=" + nodeId.substring(3)
					+ "','window', " + "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表定制</a><br/>"
					+ "</td></tr>" + "</table></td><tr></table>";
		} else {
			menuItem = "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials3"
					+ nodeId
					+ "')\" onmouseout=\"hidemenu('tutorials3"
					+ nodeId
					+ "')\">"
					+ "<a class=\"detail_mainmenu_out\" onmouseover=\"detailMainMenuOver();\" onmouseout=\"detailMainMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信息查看&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials3"
					+ nodeId
					+ "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/detail/dispatcher.jsp?id="
					+ nodeId
					+ "','window', "
					+ "'toolbar=no,height=670,width=1024,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"sbmb_menu_out\" onmouseover=\"sbmbMenuOver();\" onmouseout=\"sbmbMenuOut();\" "
					+ "onclick=\"showpanel('"
					+ ip
					+ "','"
					+ width
					+ "','"
					+ height
					+ "')"
					+ "\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备面板 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=0','window', "
					+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;生成别名拓扑图 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=1','window', "
					+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;生成IP拓扑图 </a><br/>"
					+ "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials4"
					+ nodeId
					+ "')\" onmouseout=\"hidemenu('tutorials4"
					+ nodeId
					+ "')\">"
					+ "<a class=\"set_menu_out\" onmouseover=\"setMenuOver();\" onmouseout=\"setMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;资源设置&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials4"
					+ nodeId
					+ "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\">"
					+ "<a class=\"equipRelatedApplications_menu_out\" onmouseover=\"deleteEquipRelatedApplicationsMenuOver();\" onmouseout=\"deleteEquipRelatedApplicationsMenuOut();\" "
					+ "onclick=\"addApplication('" + nodeId + "','" + ip + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备相关应用</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=inporthost&type=" + category + "&nodeId=" + nodeId + "',"
					+ " window, 'dialogwidth:600px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;导入服务器 </a><br/>"
					+ "</td></tr>" + "<tr><td class=\"menu\">" + "<a class=\"collection_menu_out\" onmouseover=\"collectionMenuOver();\" onmouseout=\"collectionMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/nodeGatherIndicators.do?action=list&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;采集指标 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"threshold_menu_out\" onmouseover=\"thresholdMenuOver();\" onmouseout=\"thresholdMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/alarmIndicatorsNode.do?action=list&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "&ipaddress=" + ip + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;指标阀值 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"port_menu_out\" onmouseover=\"portMenuOver();\" onmouseout=\"portMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/portconfig.do?action=nodeportlist&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "&ipaddress=" + ip + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口配置 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"portthreshold_menu_out\" onmouseover=\"portthresholdMenuOver();\" onmouseout=\"portthresholdMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/alarmport.do?action=list&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype=" + subtype
					+ "&ipaddress=" + ip + "'," + " window, 'dialogwidth:1000px; dialogheight:500px; status:no; help:no;resizable:0');\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口阀值 </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"portthreshold_menu_out\" onmouseover=\"portthresholdMenuOver();\" onmouseout=\"portthresholdMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/alarmIndicatorsNode.do?action=showtosetshowkeylist&type=" + type + "&nodeid=" + nodeId.substring(3) + "&subtype="
					+ subtype + "&ipaddress=" + ip + "','window', " + "'toolbar=no,height=600,width=600,scrollbars=yes,center=yes,screenY=0')\""
					+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;快捷指标 </a><br/>" + "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials1" + nodeId + "')\" onmouseout=\"hidemenu('tutorials1" + nodeId + "')\">"
					+ "<a class=\"manage_mainmenu_out\" onmouseover=\"manageMainMenuOver();\" onmouseout=\"manageMainMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备管理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials1" + nodeId + "\" width=\"135\" border=\"0\">" + "<tr><td class=\"menu\">"
					+ "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=equipProperty&type=" + category + "&nodeId=" + nodeId + "',"
					+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;图元属性 </a><br/>"
					+ "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"relationmap_menu_out\" onmouseover=\"relationMapMenuOver();\" onmouseout=\"relationMapMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=relationList&nodeId=" + nodeId + "&category=" + category + "',"
					+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;关联拓扑图 </a><br/>"
					+ "</td></tr>" + "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" "
					+ "onclick=\"deleteEquip('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统删除设备</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" "
					+ "onclick=\"removeEquip('" + nodeId + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;拓扑图删除设备</a><br/>" + "</td></tr>" + "<tr><td class=\"menu\">"
					+ "<a class=\"portscan_menu_out\" onmouseover=\"portscanMenuOver();\" onmouseout=\"portscanMenuOut();\" "
					+ "onclick=\"javascript:window.showModalDialog('/afunms/ipDistrictMatch.do?action=savePortScan&refresh=refresh&ipaddress=" + ip + "',"
					+ " window, 'dialogwidth:600px; dialogheight:400px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;端口扫描 </a><br/>"
					+ "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials2" + nodeId + "')\" onmouseout=\"hidemenu('tutorials2" + nodeId + "')\">"
					+ "<a class=\"tool_menu_out\" onmouseover=\"toolMenuOver();\" onmouseout=\"toolMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;工&nbsp;&nbsp;&nbsp;&nbsp;具&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials2" + nodeId + "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\"><a class=\"ping_menu_out\" onmouseover=\"pingMenuOver();\" onmouseout=\"pingMenuOut();\""
					+ " onclick=\"javascript:resetProcDlg();window.showModelessDialog('/afunms/tool/ping.jsp?ipaddress=" + ip + "',"
					+ " window, 'dialogHeight:500px;dialogWidth:500px;status:0;help:0;edge:sunken;center:yes;scroll:0');" + "timingCloseProDlg(8000);\" title=\"ping " + ip
					+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ping </a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"telnet_menu_out\" onmouseover=\"telnetMenuOver();\" onmouseout=\"telnetMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/network.do?action=telnet&&ipaddress=" + ip + "','window', "
					+ "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Telnet</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"trace_menu_out\" onmouseover=\"traceMenuOver();\" onmouseout=\"traceMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/tool/tracerouter.jsp?ipaddress=" + ip + "','window', "
					+ "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Traceroute</a><br/>" + "</td></tr>"

					+ "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials5" + nodeId + "')\" onmouseout=\"hidemenu('tutorials5" + nodeId + "')\">"
					+ "<a class=\"alarm_menu_out\" onmouseover=\"alarmMenuOver();\" onmouseout=\"alarmMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials5" + nodeId + "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\"><a class=\"confirmAlarm_menu_out\" onmouseover=\"confirmAlarmMenuOver();\" onmouseout=\"confirmAlarmMenuOut();\" "
					+ "onclick=\"confirmAlarm('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警确认</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/monitor.do?action=hosteventlist&nodetype=" + category.substring(0, 3) + "&id=" + nodeId.substring(3)
					+ "','window', " + "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警列表</a><br/>"
					+ "</td></tr>" + "</table></td><tr></table>"

					+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials6" + nodeId + "')\" onmouseout=\"hidemenu('tutorials6" + nodeId + "')\">"
					+ "<a class=\"report_mainmenu_out\" onmouseover=\"reportMainMenuOver();\" onmouseout=\"reportMainMenuOut();\""
					+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

					+ "<table class=\"menu\" id=\"tutorials6" + nodeId + "\" width=\"135\" border=\"0\">"

					+ "<tr><td class=\"menu\"><a class=\"report_menu_out\" onmouseover=\"reportMenuOver();\" onmouseout=\"reportMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/netreport.do?action=showPingReport&ipaddress=" + ip + "','window', "
					+ "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表查看</a><br/>" + "</td></tr>"

					+ "<tr><td class=\"menu\">" + "<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" "
					+ "onclick=\"javascript:window.open('/afunms/netreport.do?action=networkReport&nodetype=" + category.substring(0, 3) + "&id=" + nodeId.substring(3)
					+ "','window', " + "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;报表定制</a><br/>"
					+ "</td></tr>" + "</table></td><tr></table>";
		}

		return menuItem;
	}

	// 示意设备右键菜单
	public static String getMenuItem(String nodeId) {
		String menuItem = "<a class=\"deleteline_menu_out\" onmouseover=\"deleteMenuOver();\" onmouseout=\"deleteMenuOut();\" " + "onclick=\"deleteHintMeta('" + nodeId + "')"
				+ "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;删除设备</a><br/>" +

				"<a class=\"relationmap_menu_out\" onmouseover=\"relationMapMenuOver();\" onmouseout=\"relationMapMenuOut();\" "
				+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=relationList&nodeId=" + nodeId + "',"
				+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;关联拓扑图 </a><br/>";
		return menuItem;

	}

	public static String getMenuItem(String nodeId, String ip) {
		String menuItem = "<a class=\"ping_menu_out\" onmouseover=\"pingMenuOver();\" onmouseout=\"pingMenuOut();\""
				+ " onclick=\"javascript:resetProcDlg();window.showModelessDialog('/afunms/tool/ping.jsp?ipaddress="
				+ ip
				+ "',"
				+ " window, 'dialogHeight:500px;dialogWidth:500px;status:0;help:0;edge:sunken;center:yes;scroll:0');"
				+ "timingCloseProDlg(8000);\" title=\"ping "
				+ ip
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;Ping </a><br/>"
				+

				"<a class=\"trace_menu_out\" onmouseover=\"traceMenuOver();\" onmouseout=\"traceMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/tool/tracerouter.jsp?ipaddress="
				+ ip
				+ "','window', "
				+ "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;Traceroute</a><br/>"
				+ "<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" "
				+ "onclick=\"showalert('"
				+ nodeId
				+ "')"
				+ "\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>"
				+

				"<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=0','window', "
				+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;生成别名拓扑图 </a><br/>"
				+

				"<a class=\"download_menu_out\" onmouseover=\"downloadMenuOver();\" onmouseout=\"downloadMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/topology/network/download.jsp?flag=1','window', "
				+ "'toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;生成IP拓扑图 </a><br/>";

		return menuItem;
	}

	// 实体链路右键菜单
	public static String getMenuItem(String lineId, String startId, String endId) {
		String menuItem =

		"<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" " + "onclick=\"showLineInfo(" + lineId + ")\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>" +

				"<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
				+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=linkProperty&lineId=" + lineId + "',"
				+ " window, 'dialogwidth:350px; dialogheight:250px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;链路属性 </a><br/>" +

				"<a class=\"deleteline_menu_out\" onmouseover=\"deleteLineMenuOver();\" onmouseout=\"deleteLineMenuOut();\" " + "onclick=\"deleteLink(" + lineId + ")" + "\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;删除链路</a><br/>" +

				"<a class=\"editline_menu_out\" onmouseover=\"editLineMenuOver();\" onmouseout=\"editLineMenuOut();\" " + "onclick=\"editLink(" + lineId + ")\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;修改链路 </a><br/>" +

				"<a class=\"confirmAlarm_menu_out\" onmouseover=\"confirmAlarmMenuOver();\" onmouseout=\"confirmAlarmMenuOut();\" " + "onclick=\"confirmAlarmLink(" + lineId + ")"
				+ "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警确认</a><br/>";

		return menuItem;
	}

	// 示意链路右键菜单
	public static String getMenuLine(int id, String lineId) {
		String menuItem =

		"<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
				+ "onclick=\"javascript:window.showModalDialog('/afunms/link.do?action=readyEditLine&id=" + id + "',"
				+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;链路属性 </a><br/>" +

				"<a class=\"deleteline_menu_out\" onmouseover=\"deleteLineMenuOver();\" onmouseout=\"deleteLineMenuOut();\" " + "onclick=\"deleteLine('" + lineId + "')" + "\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;删除链路</a><br/>";

		return menuItem;
	}

	/**
	 * 节点类别(中文描述)
	 */
	public static String getNodeCategory(int category) {
		return getCategory(category).getCnName();
	}

	/**
	 * 节点类别(英文描述)
	 */
	public static String getNodeEnCategory(int category) {
		return getCategory(category).getEnName();
	}

	/**
	 * 根据sysOid得到服务器在拓扑图上的报警时图标
	 */
	public static String getServerAlarmImage(String sysOid) {
		String fileName = null;
		if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.1")) {
			fileName = "win_xp_alarm.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.2") || sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.3")) {
			fileName = "win_2000_alarm.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1")) {
			fileName = "win_nt_alarm.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.2021.250.10") || sysOid.equals("1.3.6.1.4.1.8072.3.2.10")) {
			fileName = "linux_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.42.")) {
			fileName = "solaris_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.2.")) {
			fileName = "ibm_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.11.")) {
			fileName = "hp_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.36.")) {
			fileName = "compaq_alarm.gif";
		} else if (sysOid.startsWith("scounix") || sysOid.startsWith("scoopenserver")) {
			fileName = "sco_red.gif";
		} else {
			fileName = "server_alarm.gif";
		}
		return "image/topo/" + fileName;
	}

	/**
	 * 根据sysOid得到服务器在拓扑图上的图标
	 */
	public static String getServerTopoImage(String sysOid) {
		String fileName = null;
		if (sysOid == null) {
			fileName = "server.gif";
			return "image/topo/" + fileName;
		}
		if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.1")) {
			fileName = "win_xp.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.2") || sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.3")) {
			fileName = "win_2000.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1")) {
			fileName = "win_nt.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.2021.250.10") || sysOid.equals("1.3.6.1.4.1.8072.3.2.10")) {
			fileName = "linux.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.42.")) {
			fileName = "solaris.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.2.")) {
			fileName = "ibm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.11.")) {
			fileName = "hp.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.36.")) {
			fileName = "compaq.gif";
		} else if (sysOid.startsWith("scounix") || sysOid.startsWith("scoopenserver")) {
			fileName = "sco.gif";
		} else {
			fileName = "server.gif";
		}
		return "image/topo/" + fileName;
	}

	/**
	 * 系统快照状态标志
	 */
	public static String getSnapStatusImage(int status) {
		String image = null;
		if (status == 1) {
			image = "status5.png";
		} else if (status == 2) {
			image = "status2.png";
		} else if (status == 3) {
			image = "status1.png";
		}
		return "image/topo/" + image;
	}

	/**
	 * 系统快照状态标志
	 */
	public static String getSnapStatusImage(int status, int category) {
		String image = null;
		if (category == 1) {
			// 路由器
			if (status == 1) {
				image = "router-alarm-1.gif";
			} else if (status == 2) {
				image = "router-alarm-1.gif";
			} else if (status == 3) {
				image = "router-alarm.gif";
			} else {
				image = "Drouter-B-32.gif";
			}

		} else if (category == 2) {
			// 交换机
			if (status == 1) {
				image = "switch-alarm-1.gif";
			} else if (status == 2) {
				image = "switch-alarm-1.gif";
			} else if (status == 3) {
				image = "switch-alarm.gif";
			} else {
				image = "Switch-B-32.gif";
			}
		} else if (category == 3) {
			// 服务器
			if (status == 1) {
				image = "server-alarm-1.gif";
			} else if (status == 2) {
				image = "server-alarm-1.gif";
			} else if (status == 3) {
				image = "server-alarm.gif";
			} else {
				image = "server-B-24.gif";
			}
		} else if (category == 4) {
			// 数据库
			if (status == 1) {
				image = "db-24-alarm-1.gif";
			} else if (status == 2) {
				image = "db-24-alarm-1.gif";
			} else if (status == 3) {
				image = "db-24-alarm.gif";
			} else {
				image = "db-24.gif";
			}
		} else if (category == 5) {
			// 中间件
			if (status == 1) {
				image = "middleware3_alarm_1.gif";
			} else if (status == 2) {
				image = "middleware3_alarm_1.gif";
			} else if (status == 3) {
				image = "middleware3_alarm.gif";
			} else {
				image = "middleware3.gif";
			}
		} else if (category == 6) {
			// 服务
			if (status == 1) {
				image = "service_alarm_1.gif";
			} else if (status == 2) {
				image = "service_alarm_1.gif";
			} else if (status == 3) {
				image = "service_alarm.gif";
			} else {
				image = "service.gif";
			}
		} else if (category == 7) {
			// 防火墙
			if (status == 1) {
				image = "firewall/firewall-alarm-1.gif";
			} else if (status == 2) {
				image = "firewall/firewall-alarm-1.gif";
			} else if (status == 3) {
				image = "firewall/firewall-alarm.gif";
			} else {
				image = "firewall/firewall.gif";
			}
		} else if (category == 8) {
			// 数据库
			if (status == 2) {
				image = "add-services-alarm.gif";
			} else {
				image = "add-services.gif";
			}
		} else if (category == 14) {
			// 防火墙
			if (status == 1) {
				image = "storage_alarm.gif";
			} else if (status == 2) {
				image = "storage_alarm.gif";
			} else if (status == 3) {
				image = "storage_alarm.gif";
			} else {
				image = "storage.gif";
			}
		}
		return "image/topo/" + image;
	}

	/**
	 * 节点状态描述
	 */
	public static String getStatusDescr(int status) {
		String descr = null;
		if (status == 0) {
			descr = "正常";
		} else if (status == 1) {
			descr = "普通告警";
		} else if (status == 2) {
			descr = "严重告警";
		} else if (status == 3) {
			descr = "紧急告警";
		} else {
			descr = "不被管理";
		}
		return descr;
	}

	/**
	 * 节点状态标志
	 */
	public static String getStatusImage(int status) {
		String image = null;
		if (status == 0) {
			image = "alert_blue.gif";
		} else if (status == 1) {
			image = "alarm_level_1.gif";
		} else if (status == 2) {
			image = "alarm_level_2.gif";
		} else if (status == 3) {
			image = "alert.gif";
		} else {
			image = "unmanaged.gif";
		}
		return "image/topo/" + image;
	}

	/**
	 * 根据sysOid得到存储在拓扑图上的图标
	 */
	public static String getStorageTopoImage(String sysOid) {
		String fileName = null;
		if (sysOid == null) {
			fileName = "storage.gif";
			return "image/topo/" + fileName;
		}
		if (sysOid.contains("1.3.6.1.4.1.1981.")) {
			fileName = "emc.gif";
		} else {
			fileName = "storage.gif";
		}
		return "image/topo/" + fileName;
	}

	public static String getSubTypeImage(String subtype) {
		String image = "";
		if (subtype.equals(Constant.TYPE_HOST_SUBTYPE_WINDOWS)) {
			image = "a_windows.gif";
		} else if (subtype.equals(Constant.TYPE_HOST_SUBTYPE_LINUX)) {
			image = "a_linux.gif";
		} else if (subtype.startsWith(Constant.TYPE_HOST_SUBTYPE_SOLARIS)) {
			image = "a_sun.gif";
		} else if (subtype.startsWith(Constant.TYPE_HOST_SUBTYPE_AIX)) {
			image = "a_aix.gif";
		} else if (subtype.startsWith(Constant.TYPE_HOST_SUBTYPE_SCOUNIX)) {
			// unixware
			image = "a_sco.gif";
		} else if (subtype.startsWith(Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER)) {
			// openserver
			image = "a_sco.gif";
		} else if (subtype.startsWith(Constant.TYPE_HOST_SUBTYPE_HPUNIX)) {
			image = "a_hp.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_HP)) {
			image = "a_hp.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_CISCO)) {
			image = "a_cisco.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_H3C)) {
			image = "a_h3c.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_ZTE)) {
			image = "a_zte.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_REDGIANT)) {
			image = "a_redg.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_DLINK)) {
			image = "a_dlink.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_BDCOM)) {
			image = "a_bdcom.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_MAIPU)) {
			image = "a_maip.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_AVAYA)) {
			image = "a_avaya.gif";
		} else if (subtype.startsWith(Constant.TYPE_NET_SUBTYPE_ALCATEL)) {
			image = "a_alcatel.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_MYSQL)) {
			image = "mysql.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_ORACLE)) {
			image = "oracle.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_ORACLERAC)) {
			// rac
			image = "oracle.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_SQLSERVER)) {
			image = "sqlserver.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_SYBASE)) {
			image = "sybase.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_DB2)) {
			image = "db2.gif";
		} else if (subtype.equals(Constant.TYPE_DB_SUBTYPE_INFORMIX)) {
			image = "informix.gif";
		} else if (subtype.equals("ggsn")) {
			image = "GGSN.JPG";
		} else if (subtype.equals("sgsn")) {
			image = "SGSN.JPG";
		} else if (subtype.equals("91")) {
			image = "GGSN.JPG";
		} else if (subtype.equals("92")) {
			image = "SGSN.JPG";
		} else if ("as400".equals(subtype)) {
			image = "a_as400.gif";
		} else if (subtype.equals(Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS)) {
			image = "arraynetworks.gif";
		} else if (subtype.equals(Constant.TYPE_F5_SUBTYPE_F5)) {
			image = "f5.gif";
		} else if (subtype.equals(Constant.TYPE_GATEWAY_SUBTYPE_CISCO)) {
			image = "ironport.gif";
		} else if (subtype.equals(Constant.TYPE_ATM_SUBTYPE_ATM)) {
			image = "atm.gif";
		} else if (subtype.equals(Constant.TYPE_ATM_SUBTYPE_CMTS)) {
			image = "cmts.gif";
		} else if (subtype.equals(Constant.TYPE_NET_MOTOROLA)) {
			image = "a_motorola.gif";
		} else if (subtype.equals(Constant.TYPE_NET_SUBTYPE_NORTHTEL)) {
			image = "a_northtel.gif";
		} else if (subtype.equals(Constant.TYPE_VIRTUAL_SUBTYPE_VMWARE)) {
			image = "vmware.gif";
		} else if (subtype.equals(Constant.TYPE_NET_SUBTYPE_JUNIPER)) {
			image = "a_juniper.gif";
		} else if (subtype.equals(Constant.TYPE_NET_SUBTYPE_BROCADE)) {
			image = "a_ibm.gif";
		} else {
			image = "unmanaged.gif";
		}
		return "dtree/img/" + image;
	}

	/**
	 * 节点在拓扑图上的图标
	 */
	public static String getTopoImage(int category) {
		return getCategory(category).getTopoImage();
	}

	/**
	 * 节点在拓扑图上的图标
	 */
	public static String getTopoImage(String category) {
		return getCategory(category).getTopoImage();
	}

	public static String getTypeImage(String category) {
		String image = "";
		if ("net_switch".equals(category)) {
			image = "switch.gif";
		} else if ("middleware".equals(category)) {
			image = "mid.gif";
		} else if ("net_f5".equals(category)) {
			image = "fz.gif";
		} else if ("net_gateway".equals(category)) {
			image = "ironport.gif";
		} else if ("net_server".equals(category)) {
			image = "host.gif";
		} else if ("safeequip".equals(category)) {
			image = "safe.gif";
		} else if ("net_firewall".equals(category)) {
			image = "firewall.gif";
		} else if ("services".equals(category)) {
			image = "service.gif";
		} else if ("dp".equals(category)) {
			image = "service.gif";
		} else if ("nas".equals(category)) {
			image = "service.gif";
		} else if ("ggsci".equals(category)) {
			image = "service.gif";
		} else if ("ntp".equals(category)) {
			image = "service.gif";
		} else if ("bussiness".equals(category)) {
			image = "bus.gif";
		} else if ("interface".equals(category)) {
			image = "int.gif";
		} else if ("storage".equals(category)) {
			image = "store.gif";
		} else if ("ups".equals(category)) {
			image = "ups.gif";
		} else if ("net_router".equals(category)) {
			image = "route.gif";
		} else if ("dbs".equals(category)) {
			image = "dbs.gif";
		} else if ("tomcat".equals(category)) {
			image = "tomcat.gif";
		} else if ("weblogic".equals(category)) {
			image = "weblogic.gif";
		} else if ("mail".equals(category)) {
			image = "email.gif";
		} else if ("ftp".equals(category)) {
			image = "ftp.gif";
		} else if ("tftp".equals(category)) {
			image = "tftp.gif";
		} else if ("dhcp".equals(category)) {
			image = "dhcp.gif";
		} else if ("wes".equals(category)) {
			image = "web.gif";
		} else if ("wblogin".equals(category)) {
			image = "web.gif";
		} else if ("socket".equals(category)) {
			image = "port.gif";
		} else if ("domino".equals(category)) {
			image = "domino.gif";
		} else if ("cics".equals(category)) {
			image = "cics.gif";
		} else if ("dns".equals(category)) {
			image = "dns.gif";
		} else if ("apache".equals(category)) {
			image = "apache.gif";
		} else if ("tuxedo".equals(category)) {
			image = "tuxedo.gif";
		} else if ("jboss".equals(category)) {
			image = "jboss.gif";
		} else if ("iis".equals(category)) {
			image = "iis.gif";
		} else if ("mqs".equals(category)) {
			image = "mq_js.gif";
		} else if ("was".equals(category)) {
			image = "was.gif";
		} else if ("resin".equals(category)) {
			image = "resin.png";
		} else if ("as400".equals(category)) {
			image = "a_as400.gif";
		} else if ("net_atm".equals(category)) {
			image = "atm.gif";
		} else if ("net_cmts".equals(category)) {
			image = "cmts.gif";
		} else if ("net_vpn".equals(category)) {
			image = "vpn.gif";
		} else if (category.equals(Constant.TYPE_F5_SUBTYPE_F5)) {
			image = "f5.gif";
		} else if ("virtual".equals(category)) {
			image = "virtual.gif";
		} else if ("net_vmware".equals(category)) {
			image = "vmware.gif";
		} else if ("net_storage".equals(category)) {
			image = "ccxx.gif";
		} else if (category.equals("1.3.6.1.4.1.311.1.1.3.1.1")) {
			image = "a_windows.gif";
		} else if (category.equals("1.3.6.1.4.1.311.1.1.3.1.2") || category.equals("1.3.6.1.4.1.311.1.1.3.1.3") || category.equals("1.3.6.1.4.1.99.1.1.3.11")) {
			image = "a_windows.gif";
		} else if (category.equals("1.3.6.1.4.1.311.1.1.3.1")) {
			image = "a_windows.gif";
		} else if (category.equals("1.3.6.1.4.1.311.1.1.3")) {
			image = "a_windows.gif";
		} else if (category.equals("1.3.6.1.4.1.2021.250.10") || category.equals("1.3.6.1.4.1.8072.3.2.10")) {
			image = "a_linux.gif";
		} else if (category.startsWith("1.3.6.1.4.1.42.")) {
			image = "a_sun.gif";
		} else if (category.startsWith("1.3.6.1.4.1.2.")) {
			image = "a_aix.gif";
		} else if (category.startsWith("1.3.6.1.4.1.11.")) {
			image = "a_hp.gif";
		} else if (category.startsWith("scounix") || category.startsWith("scoopenserver")) {
			image = "a_sco.gif";
		} else if (category.startsWith("1.3.6.1.4.1.9.")) {
			image = "a_cisco.gif";
		} else if (category.startsWith("1.3.6.1.4.1.25506")) {
			image = "a_h3c.gif";
		} else if (category.startsWith("1.3.6.1.4.1.2011")) {
			image = "a_h3c.gif";
		} else if (category.startsWith("1.3.6.1.4.1.4881.")) {
			image = "a_redg.gif";
		} else if (category.startsWith("1.3.6.1.4.1.5651.")) {
			image = "a_maip.gif";
		} else if (category.startsWith("1.3.6.1.4.1.171.")) {
			image = "a_dlink.gif";
		} else if (category.startsWith("1.3.6.1.4.1.2272.") || category.startsWith("1.3.6.1.4.1.45.")) {
			image = "a_northtel.gif";
		} else if (category.startsWith("1.3.6.1.4.1.89.")) {
			image = "a_rad.gif";
		} else if (category.startsWith("1.3.6.1.4.1.3902.")) {
			image = "a_zte.gif";
		} else if (category.startsWith("1.3.6.1.4.1.2636.")) {
			image = "a_juniper.gif";
		} else if (category.startsWith("1.3.6.1.4.1.1588.2.1.1.")) {
			image = "a_ibm.gif";
		} else if (category.startsWith("1.3.6.1.4.1.3320")) {
			image = "a_bdcom.gif";
		} else if (category.startsWith("1.3.6.1.4.1.388.11.1.2")) {
			image = "a_motorola.gif";
		} else if (category.equals("4")) {
			image = "host.gif";
		} else if (category.equals("2") || category.equals("3") || category.equals("7")) {
			image = "switch.gif";
		} else if (category.equals("1")) {
			image = "route.gif";
		} else if (category.equals("9")) {
			image = "atm.gif";
		} else if (category.equals("13")) {
			image = "cmts.gif";
		} else if (category.equals("52")) {
			image = "mysql.gif";
		} else if (category.equals("53")) {
			image = "oracle.gif";
		} else if (category.equals("65")) {
			image = "oracle.gif";
		} else if (category.equals("54")) {
			image = "sqlserver.gif";
		} else if (category.equals("55")) {
			image = "sybase.gif";
		} else if (category.equals("59")) {
			image = "db2.gif";
		} else if (category.equals("60")) {
			image = "informix.gif";
		} else if (category.equals("ggsn")) {
			image = "GGSN.JPG";
		} else if (category.equals("sgsn")) {
			image = "SGSN.JPG";
		} else if (category.equals("91")) {
			image = "GGSN.JPG";
		} else if (category.equals("92")) {
			image = "SGSN.JPG";
		} else {
			image = "unmanaged.gif";
		}
		return "dtree/img/" + image;
	}

	/**
	 * <P>
	 * 先更新节点数据(CPU、内存等)
	 * </p>
	 * HONGLI
	 * 
	 * @param monitornodelist
	 */
	public void collectData(List nodeList) {
		String runmodel = PollingEngine.getCollectwebflag();
		if ("1".equals(runmodel)) {
			// 采集与访问是分离模式
			NetworkDao networkDao = new NetworkDao();
			try {
				networkDao.collectAllNetworkData(nodeList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}