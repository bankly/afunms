package com.afunms.emc.model;

import java.util.List;

import com.afunms.common.base.BaseVo;

public class Lun extends BaseVo {
	private String id;
	private String nodeid;
	private String rgid;
	private String name; // ����
	private String RAIDType; // RAID����
	private String RAIDGroupID; // ������RAID��ID
	private String State; // ״̬
	private String currentOwner; // ��ǰ������
	private String defaultOwner; // Ĭ��������
	private String writecache; // �Ƿ�д����
	private String readcache; // �Ƿ������
	private String prctRebuilt; // �����ؽ��ٷֱ�
	private String prctBound; // ���̰󶨰ٷֱ�
	private String LUNCapacity; // ������MB��
	private List<String> disksList; // �󶨵Ĵ����б�
	private String disklistStr;
	private int totalHardErrors; // Ӳ����������
	private int totalSoftErrors; // ������������
	private int totalQueueLength; // �����ܳ���

	public String getCurrentOwner() {
		return currentOwner;
	}

	public String getDefaultOwner() {
		return defaultOwner;
	}

	public String getDisklistStr() {
		return disklistStr;
	}

	public List<String> getDisksList() {
		return disksList;
	}

	public String getId() {
		return id;
	}

	public String getLUNCapacity() {
		return LUNCapacity;
	}

	public String getName() {
		return name;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getPrctBound() {
		return prctBound;
	}

	public String getPrctRebuilt() {
		return prctRebuilt;
	}

	public String getRAIDGroupID() {
		return RAIDGroupID;
	}

	public String getRAIDType() {
		return RAIDType;
	}

	public String getReadcache() {
		return readcache;
	}

	public String getRgid() {
		return rgid;
	}

	public String getState() {
		return State;
	}

	public int getTotalHardErrors() {
		return totalHardErrors;
	}

	public int getTotalQueueLength() {
		return totalQueueLength;
	}

	public int getTotalSoftErrors() {
		return totalSoftErrors;
	}

	public String getWritecache() {
		return writecache;
	}

	public void setCurrentOwner(String currentOwner) {
		this.currentOwner = currentOwner;
	}

	public void setDefaultOwner(String defaultOwner) {
		this.defaultOwner = defaultOwner;
	}

	public void setDisklistStr(String disklistStr) {
		this.disklistStr = disklistStr;
	}

	public void setDisksList(List<String> disksList) {
		this.disksList = disksList;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLUNCapacity(String capacity) {
		LUNCapacity = capacity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setPrctBound(String prctBound) {
		this.prctBound = prctBound;
	}

	public void setPrctRebuilt(String prctRebuilt) {
		this.prctRebuilt = prctRebuilt;
	}

	public void setRAIDGroupID(String groupID) {
		RAIDGroupID = groupID;
	}

	public void setRAIDType(String type) {
		RAIDType = type;
	}

	public void setReadcache(String readcache) {
		this.readcache = readcache;
	}

	public void setRgid(String rgid) {
		this.rgid = rgid;
	}

	public void setState(String state) {
		State = state;
	}

	public void setTotalHardErrors(int totalHardErrors) {
		this.totalHardErrors = totalHardErrors;
	}

	public void setTotalQueueLength(int totalQueueLength) {
		this.totalQueueLength = totalQueueLength;
	}

	public void setTotalSoftErrors(int totalSoftErrors) {
		this.totalSoftErrors = totalSoftErrors;
	}

	public void setWritecache(String writecache) {
		this.writecache = writecache;
	}

}