<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.profile.image.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
</head>
<body>
<!-- Navigation Section including sub nav menu -->
<#include "../template/nav_section.ftl" />
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/admin/displayUserHome.jspx"><@s.text name="user.display.home.action.title" /></a></div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><@s.text name="user.profile.image.action.title" /></div>		
</div>
<div style="clear:both"></div> 
<div class="main_body_container">
<div class="main_big_border">
	<div class="left_container_panel">
		<br/>
		<div class="left_middle_panel">
			<#include "../template/action_errors.ftl" /> 
            <div class="none_border_block"></div>
		 	<div class="dotted_border_div">
		 		<br/>
		 		<div class="none_border_block3">
					<@s.form action="uploadImage.jspx" namespace="/admin" method="post" enctype="multipart/form-data" >
						<span>Upload Your Profile Image <@s.file name="image"  />
							&nbsp;&nbsp;<@s.submit value=" Upload " cssClass="input_button_normal" />
						</span>
						
					</@s.form>
					<div class="blank_separator"></div>
					<span class="line_comments">(Only the <b>jpg</b>, <b>png</b> and <b>gif</b> image formats are supported. The minimum image size:[48x48] )</span> 
				</div>
				<br/>
			</div>
			<div class="none_border_space_block"></div>
			<div style="clear:both"></div>
		</div>
		<br/>
		
	</div>
	<div class="right_container_panel">		 
		 <#include "../template/subnav_section.ftl" />
	</div>
	<div style="clear:both"></div>
</div> 
</div>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>