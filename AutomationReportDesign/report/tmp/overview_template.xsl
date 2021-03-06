<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/html4/strict.dtd" doctype-public="-//W3C//DTD HTML 4.01//EN" indent="yes" />
	<xsl:template match="/">
<html>
<head>
<title>Automation Tool Test Case Report</title>

<link rel="stylesheet" type="text/css" href="css/report.css" />

<script type="text/javascript" src="js/jquery-1.7.2.js"></script>

<script type="text/javascript" language="JavaScript">
function registerToggle(){
	$('.tc').bind('click', function(){
		$(this).parent().parent().find('div').slideToggle('slow');
		if($(this).attr('src') == 'images/sub.jpg'){
			$(this).attr('src', 'images/add.jpg');
		}else{
			$(this).attr('src', 'images/sub.jpg');
		}
	});
	$('.togglepoint').hide();
	$('.togglepoint').slideDown('fast');
}

$(document).ready(function() {
	
	var hrefstr = $(location).attr('href'); 
	var find = hrefstr.indexOf('#');
	if(find >0 &amp;&amp; (find+1) &lt; hrefstr.length)
		hrefstr = hrefstr.substring(find+1, hrefstr.length);
	else
		hrefstr = 1;
	
	$("#content").load("testcase"+hrefstr+".html", function(){
		registerToggle();
	});

	$("a[id^=testcase]").click(function(){
		$("#content").load($(this).attr('id')+".html", function(){
			registerToggle();
		});
	});
	
});

</script>

</head>
<body>

<div id="wrapper">

	<div id="page">
	
		<div id="sidebar">
			<div class="box">
				<ul class="list">
				<xsl:for-each select="overview/testcase">
				
				<xsl:choose>
				<xsl:when test="position() = 1">
				<li class="first">
					<xsl:choose>
					<xsl:when test="self::node()[@status='pass']">
					<img src="images/testcase_pass.jpg"/>
					</xsl:when>
					<xsl:otherwise>
					<img src="images/testcase_fail.jpg"/>
					</xsl:otherwise>
					</xsl:choose>
				<a href="#{position()}" id='{self::node()[text()]}'><xsl:value-of select="self::node()[text()]"/></a>
				</li>
				</xsl:when>
				
				<xsl:when test="position() = last()">
				<li class="last">
					<xsl:choose>
					<xsl:when test="self::node()[@status='pass']">
					<img src="images/testcase_pass.jpg"/>
					</xsl:when>
					<xsl:otherwise>
					<img src="images/testcase_fail.jpg"/>
					</xsl:otherwise>
					</xsl:choose>
				<a href="#{position()}" id='{self::node()[text()]}'><xsl:value-of select="self::node()[text()]"/></a>
				</li>
				</xsl:when>
				
				<xsl:otherwise>
				<li>
					<xsl:choose>
					<xsl:when test="self::node()[@status='pass']">
					<img src="images/testcase_pass.jpg"/>
					</xsl:when>
					<xsl:otherwise>
					<img src="images/testcase_fail.jpg"/>
					</xsl:otherwise>
					</xsl:choose>
				<a href="#{position()}" id='{self::node()[text()]}'><xsl:value-of select="self::node()[text()]"/></a>
				</li>
				</xsl:otherwise>
				</xsl:choose>

				</xsl:for-each>
				</ul>
			</div>
		</div>
	
		<div id="content">
			
		</div>
	</div>
</div>

</body>
</html>
	</xsl:template>
</xsl:stylesheet>