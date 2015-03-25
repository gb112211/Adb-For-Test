package xuxu.autotest.element;

//存储解析xml文件后得到的属性值ֵ
public class UiDump {
	private String text;
	private String resourceId;
	private String className;
	private String contentDesc;
	private String checkable;
	private String checked;
	private String clickable;
	private String bounds;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getBounds() {
		return bounds;
	}

	public void setBounds(String bounds) {
		this.bounds = bounds;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getContentDesc() {
		return contentDesc;
	}

	public void setContentDesc(String contentDesc) {
		this.contentDesc = contentDesc;
	}

	public String getCheckable() {
		return checkable;
	}

	public void setCheckable(String checkable) {
		this.checkable = checkable;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getClickable() {
		return clickable;
	}

	public void setClickable(String clickable) {
		this.clickable = clickable;
	}

	@Override
	public String toString() {
		return "UiDump [text=" + text + ", resourceId=" + resourceId
				+ ", className=" + className + ", packageName="
				+ ", contentDesc=" + contentDesc + ", checkable=" + checkable
				+ ", checked=" + checked + ", clickable=" + clickable
				+ ", bounds=" + bounds + "]";
	}

}
