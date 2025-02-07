<div style="clear:both"></div>
<div class="blank_separator"></div>
<div class="blank_separator"></div>
<#include "use_policy.ftl"/>
<div style="clear:both"></div>
<div class="blank_separator"></div>
<div class="blank_separator"></div>
<#include "ands.ftl"/>
<div class="blank_separator"></div>
<div class="blank_separator"></div>
<div style="clear:both"></div>
<#include "sponsor.ftl"/>
<div class="blank_separator"></div>
<div class="blank_separator"></div>
<div style="clear:both"></div>
<script>
    $(document).ready(function () {
        $("#current_year").html(new Date().getFullYear());
    })
</script>
<div class="footer">
    <br/>
    <div class="copyright">
        Copyright &copy; 2010-<span id="current_year"></span> Monash University. All Rights Reserved.
        &nbsp;&nbsp;&nbsp;&nbsp;Powered by <a href="http://code.google.com/p/eddy/"
                                              target="_blank">Eddy <@s.text name="app.version" /></a>
    </div>
    <br/>
</div>
<br/>