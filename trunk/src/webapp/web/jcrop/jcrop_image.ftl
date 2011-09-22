<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.display.home.action.title" /> - <@s.text name="user.profile.image.action.title" /></title>
<#include "../template/jquery_header.ftl"/>
<script language="Javascript">
		jQuery(window).load(function(){
				jQuery('#cropbox').Jcrop({
					onChange: showPreview,
					onSelect: showPreview,
					aspectRatio: 1,
					bgOpacity:   .8,
					setSelect: [0,0,50,50],
					minSize: [50,50],
				});

			});
			var imageWidth= <@s.property value='imgWidth' />;
			var imageHeight = <@s.property value='imgHeight' />;
			
			// Our simple event handler, called from onChange and onSelect
			// event handlers, as per the Jcrop invocation above
			function showPreview(coords)
			{
				if (parseInt(coords.w) > 0)
				{
					var rx = 48 / coords.w; 
					var ry = 48 / coords.h; 

					jQuery('#preview').css({
						width: Math.round(rx * imageWidth) + 'px',
						height: Math.round(ry * imageHeight) + 'px',
						marginLeft: '-' + Math.round(rx * coords.x) + 'px',
						marginTop: '-' + Math.round(ry * coords.y) + 'px'
					});
					//set the coordinates.
					jQuery('#imageX1').val(coords.x);
        			jQuery('#imageY1').val(coords.y);
        			jQuery('#imageX2').val(coords.x2);
        			jQuery('#imageY2').val(coords.y2);
        			jQuery('#imageW').val(coords.w);
        			jQuery('#imageH').val(coords.h);
				}
			}

	</script>
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
		 	  	<table>
					<tr>
						<td>
							<div class="upload_profile_img">
							<img src="${base}/<@s.property value='userImageName' />" id="cropbox" />
							</div>
						</td>
						<td width="20">&nbsp;</td>
						<td>
							<div style="width:48px;height:48px;overflow:hidden;">
								<img src="${base}/<@s.property value='userImageName' />" id="preview" />
							</div>
						</td>
					</tr>
					<tr><td>&nbsp;</td><td></td><td></td></tr>
					<tr>
						<td colspan="3">
							<@s.form action="saveAvatar.jspx" namespace="/admin" method="post">
								<@s.hidden name="imageX1" id="imageX1"/>
							    <@s.hidden name="imageY1" id="imageY1"/>
							    <@s.hidden name="imageX2" id="imageX2"/>
							    <@s.hidden name="imageY2" id="imageY2"/>
							    <@s.hidden name="imageW" id="imageW"/>
							    <@s.hidden name="imageH" id="imageH"/>
							    <@s.hidden name="userImageName" />
							    <@s.hidden name="imgWidth" />
							    <@s.hidden name="imgHeight" />
							    <div class="input_button_div">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<@s.submit value=" Save " cssClass="input_button_style" /></div>
						    </@s.form>
						</td>
					</tr>
					<tr><td></td><td></td><td></td></tr>
				</table>
				<div class="blank_separator"></div>
			</div>
			<div class="none_border_block"></div>
			<div style="clear:both"></div>
		</div>
		<br/>
		<div style="clear:both"></div>
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