<#assign s=JspTaglibs["/WEB-INF/struts-tags.tld"] />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><@s.text name="user.register.option.title" /></title>

<#include "../template/header.ftl"/>

</head>
<body>
<!-- Navigation Section -->
<#include "../template/nav_section.ftl" />
<!-- Navigation Title -->
<div class="title_panel">
	<div class="div_inline">&nbsp;&nbsp;</div>
	<div class="div_inline"><img src="${base}/images/link_arrow.png" border="0"/></div>
	<div class="div_inline"><a href="${base}/user/register_options"><@s.text name="user.register.option.title" /></a></div>
</div>
<div style="clear:both"></div> 
<!-- End of Navigation Title -->
		
<div class="main_body_container">
	<div class="main_body_big_left_panel">
	<br />
		<#include "../template/action_errors.ftl" />		
        <div class="reg_options_div">
            <div class="reg_options_middle">
                <div>
                    <@s.text name="user.reg.choose.options.msg" />
                </div>
                <br/>
                <br/>
                <div class="blank_separator"></div>
                <div class="blank_separator"></div>
                <div class="blank_separator"></div>
                <div class="reg_choices">
				    <a href="${base}/user/ldap_user_register"><img src="${base}/images/mon_reg.png"  border="0" /> <strong><@s.text name="user.ldap.register.action.title" /></strong></a>
				</div>
                <div style="clear:both"></div>
                <div class="blank_separator"></div>
                <br/>
                <br/>
                <div class="blank_separator"></div>
                <div class="reg_choices">
				    <a href="${base}/user/user_register"><img src="${base}/images/self_reg.png" border="0" /> <strong><@s.text name="user.register.action.title" /></strong></a>
				</div>
                <div style="clear:both"></div>
                <br/>
            </div>
            <br/>
        </div>
        <br/>
        <div class="use_policy_outer">
            <div class="reg_options_middle">
                <div class="use_policy">
                    This site is provided 'as is' by Monash University for use by Monash researchers and their research collaborators at other institutions. Use by Monash's research collaborators
                    is encouraged and welcomed. Use of this site by all users is subject to Monash University's normal Staff Acceptable Use Policy for IT Services (AUP) available
                    at:<a href="http://www.policy.monash.edu/policy-bank/management/its/it-use-policy-staff-and-authorised.html" target="_blank">www.policy.monash.edu/policy-bank/management/its/it-use-policy-staff-and-authorised.html</a>.
                    The Monash team leader of each research group is responsible for ensuring that the AUP is adhered to by all members of the group. Publication of information, information access controls,
                    group membership controls are the responsibility of the team leader of each worksite. Monash University does not warrant the accuracy of the information provided by the service, nor the
                    fitness for purpose of the service for your intended application. Data is stored and backed-up on the University's LaRDS research data store. Services are provided to 3rd parties on an 'all
                    care no responsibility' basis. Use of this site indicates your acceptance of these terms and conditions.
                </div>
            </div>
        </div>
        <br/>
	</div>
	<div style="clear:both"></div>  		
</div>
<br/>
<br/>
<#include "../template/footer.ftl"/>
</body>
</html>
