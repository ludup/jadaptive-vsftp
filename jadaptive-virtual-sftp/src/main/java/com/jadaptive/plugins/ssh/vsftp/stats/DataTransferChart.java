package com.jadaptive.plugins.ssh.vsftp.stats;

import java.net.URL;
import java.util.Objects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;

import com.jadaptive.api.ui.DashboardWidget;
import com.jadaptive.api.ui.PageHelper;

@Extension
public class DataTransferChart implements DashboardWidget {

	@Override
	public String getIcon() {
		return "chart-column";
	}

	@Override
	public String getBundle() {
		return "virtualFolder";
	}

	@Override
	public String getName() {
		return "dataTransfer";
	}

	@Override
	public void renderWidget(Document document, Element element) {
		
		PageHelper.appendScript(document, "/app/content/amcharts5/index.js");
		PageHelper.appendScript(document, "/app/content/amcharts5/xy.js");
		PageHelper.appendScript(document, "/app/content/amcharts5/themes/Animated.js");
		
		element.appendChild(new Element("div").attr("id", "chart1").addClass("w-100").attr("style", "height: 500px;"));

		URL url = getClass().getResource(String.format("%s.js", getClass().getSimpleName()));
		if(Objects.nonNull(url)) {
			PageHelper.appendScript(document, "/app/script/" + getClass().getPackageName().replace('.', '/') + "/" + getClass().getSimpleName() + ".js");
		} 
	}

	@Override
	public Integer weight() {
		return 0;
	}

	@Override
	public boolean wantsDisplay() {
		return true;
	}

}
