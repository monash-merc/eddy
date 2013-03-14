/**hidden the message pane*/
$(document).ready(function () {
    $(".file_success_msg_div .hidden_msg").click(function () {
        $(this).parents(".file_success_msg_div").hide();
    });
});

/**remove the message pane*/
$(document).ready(function () {
    $(".pane .delete").click(function () {
        $(this).parents(".pane").remove();
    });
});


/**remove the message pane*/
$(document).ready(function () {
    $(".pop_pane .delete").click(function () {
        $(this).parents(".pop_pane").remove();
    });
});

/** pagination orderby */

$(document).ready(function () {
    $("#item_select_size").change(function () {
        // var message_index = $("#item_select_size").val();
        // alert(message_index)
        window.location.href = $('.page_url').attr('href') + "&sizePerPage=" + $("#item_select_size").val();
    });
});

$(document).ready(function () {
    $("#item_select_order").change(function () {
        window.location.href = $('.page_url').attr('href') + "&orderBy=" + $("#item_select_order").val() + "&orderByType=" + $("#item_select_otype").val();
    });
});

$(document).ready(function () {
    $("#item_select_otype").change(function () {
        window.location.href = $('.page_url').attr('href') + "&orderBy=" + $("#item_select_order").val() + "&orderByType=" + $("#item_select_otype").val();
    });
});

$(function () {
    $('a#viewdataset').click(function (e) {
        e.preventDefault();
        var $this = $(this);
        var horizontalPadding = 30;
        var verticalPadding = 30;

        $('<iframe id="externalSite" class="externalSite" src="' + this.href + '" />').dialog({
            title:($this.attr('title')) ? $this.attr('title') : 'External Site',
            autoOpen:true,
            width:730,
            height:400,
            modal:true,
            resizable:true,
            autoResize:true,
            overlay:{
                opacity:0.5,
                background:"black"
            }
        }).width(730 - horizontalPadding).height(400 - verticalPadding);
    });
});

$(document).ready(function () {
    $("#add_permission").click(function () {
        var value_index = $("#selected_username").val();
        var selectedText = $('#selected_username option:selected').text();
        var rowIndex = $("#user_permissions > tbody > tr").length;

        //if an user permissions already added, just return.
        var el = $("input[id=user_id][value=" + value_index + "]").val();
        if (el != null) {
            return;
        }

        if (value_index != '-1') {
            $('#user_permissions > tbody:last').append("<tr>" +
                "<td>" + selectedText + "" +
                "<input type='hidden' name='permissionBeans[" + rowIndex + "].id' value='0' id='permissionBeans_" + rowIndex + "__id'/>" +
                "<input type='hidden' name='permissionBeans[" + rowIndex + "].uid' value='" + value_index + "' id='user_id'/></td>" +
                "<input type='hidden' name='permissionBeans[" + rowIndex + "].userName' value='" + selectedText + "' id='permissionBeans_" + rowIndex + "__userName'/></td>" +
                "<td><input type='checkbox' class='check_box' name='permissionBeans[" + rowIndex + "].viewAllowed' value='true' id='permissionBeans_" + rowIndex + "__viewAllowed'/></td>" +
                "<td><input type='checkbox' class='check_box' name='permissionBeans[" + rowIndex + "].editAllowed' value='true' id='permissionBeans_" + rowIndex + "__editAllowed'/></td>" +
                "<td><input type='checkbox' class='check_box' name='permissionBeans[" + rowIndex + "].importAllowed' value='true'  id='permissionBeans_" + rowIndex + "__importAllowed'/></td>" +
                "<td><input type='checkbox' class='check_box' name='permissionBeans[" + rowIndex + "].exportAllowed' value='true'  id='permissionBeans_" + rowIndex + "__exportAllowed'/></td>" +
                "<td><input type='checkbox' class='check_box' name='permissionBeans[" + rowIndex + "].deleteAllowed' value='true'  id='permissionBeans_" + rowIndex + "__deleteAllowed'/></td>" +
                "<td><input type='checkbox' class='check_box' name='permissionBeans[" + rowIndex + "].changePermAllowed' value='true'  id='permissionBeans_" + rowIndex + "__changePermAllowed'/></td>" +
                "</tr>");
        }
    });
});


$("#user_permissions input[type=checkbox]").live('click', function () {
    var act = $(this).attr('name');
    if ($(this).is(":checked")) {
        //alert(act);
        if (act == 'coPermForAllUser.editAllowed' || act == 'coPermForAllUser.importAllowed' || act == 'coPermForAllUser.exportAllowed' || act == 'coPermForAllUser.deleteAllowed' || act == 'coPermForAllUser.changePermAllowed') {
            setViewTrueForAllRegUser();
        }
        if (act == 'coPermForAnony.editAllowed' || act == 'coPermForAnony.importAllowed' || act == 'coPermForAnony.exportAllowed' || act == 'coPermForAnony.deleteAllowed' || act == 'coPermForAnony.changePermAllowed') {
            setViewTrueForAnonymous();
        }
        //for dynamically added user permission
        if ((act.indexOf('permissionBeans') != -1) && (act.indexOf('editAllowed') != -1)) {
            setViewAllowedTrueForDyn(act);
        }
        if ((act.indexOf('permissionBeans') != -1) && (act.indexOf('importAllowed') != -1)) {
            setViewAllowedTrueForDyn(act);
        }
        if ((act.indexOf('permissionBeans') != -1) && (act.indexOf('exportAllowed') != -1)) {
            setViewAllowedTrueForDyn(act);
        }
        if ((act.indexOf('permissionBeans') != -1) && (act.indexOf('deleteAllowed') != -1)) {
            setViewAllowedTrueForDyn(act);
        }
        if ((act.indexOf('permissionBeans') != -1) && (act.indexOf('changePermAllowed') != -1)) {
            setViewAllowedTrueForDyn(act);
        }
    } else {//for dynamic added checkbox
        if ((act.indexOf('permissionBeans') != -1) && (act.indexOf('viewAllowed') != -1)) {
            foreViewAllowedTrueForDyn(act);
        }
    }
});
//for all-registered users
$("input[name='coPermForAllUser.viewAllowed']").live('click', function () {
    if ($(this).is(":checked") == false) {
        if ($("input[name='coPermForAllUser.editAllowed']").is(":checked")) {
            keepViewAllowedTrueForAllUsers();
        }
        if ($("input[name='coPermForAllUser.importAllowed']").is(":checked")) {
            keepViewAllowedTrueForAllUsers();
        }
        if ($("input[name='coPermForAllUser.exportAllowed']").is(":checked")) {
            keepViewAllowedTrueForAllUsers();
        }
        if ($("input[name='coPermForAllUser.deleteAllowed']").is(":checked")) {
            keepViewAllowedTrueForAllUsers();
        }
        if ($("input[name='coPermForAllUser.changePermAllowed']").is(":checked")) {
            keepViewAllowedTrueForAllUsers();
        }
    }
});

//for all-annonymous user
$("input[name='coPermForAnony.viewAllowed']").live('click', function () {
    if ($(this).is(":checked") == false) {
        if ($("input[name='coPermForAnony.editAllowed']").is(":checked")) {
            keepViewAllowedTrueForAnonymous();
        }
        if ($("input[name='coPermForAnony.importAllowed']").is(":checked")) {
            keepViewAllowedTrueForAnonymous();
        }
        if ($("input[name='coPermForAnony.exportAllowed']").is(":checked")) {
            keepViewAllowedTrueForAnonymous();
        }
        if ($("input[name='coPermForAnony.deleteAllowed']").is(":checked")) {
            keepViewAllowedTrueForAnonymous();
        }
        if ($("input[name='coPermForAnony.changePermAllowed']").is(":checked")) {
            keepViewAllowedTrueForAnonymous();
        }
    }
});
//for all-registered users
function setViewTrueForAllRegUser() {
    $("input[name='coPermForAllUser.viewAllowed']").attr('checked', true);
}
;
function keepViewAllowedTrueForAllUsers() {
    $("input[name='coPermForAllUser.viewAllowed']").attr('checked', true);
}
;

//for anonymous users
function setViewTrueForAnonymous() {
    $("input[name='coPermForAnony.viewAllowed']").attr('checked', true);
}
;

function keepViewAllowedTrueForAnonymous() {
    $("input[name='coPermForAnony.viewAllowed']").attr('checked', true);
}
;

//for dynamic added checkbox
function setViewAllowedTrueForDyn(name) {
    var strv = name.substring(0, name.indexOf('.'));
    $('input[name=' + strv + '.viewAllowed]').attr('checked', true);
}
;

function foreViewAllowedTrueForDyn(name) {
    var strv = name.substring(0, name.indexOf('.'));
    if ($('input[name=' + strv + '.editAllowed]').is(":checked")) {
        keepViewAllowedTrueForDyn(strv);
    }
    if ($('input[name=' + strv + '.importAllowed]').is(":checked")) {
        keepViewAllowedTrueForDyn(strv);
    }
    if ($('input[name=' + strv + '.exportAllowed]').is(":checked")) {
        keepViewAllowedTrueForDyn(strv);
    }
    if ($('input[name=' + strv + '.deleteAllowed]').is(":checked")) {
        keepViewAllowedTrueForDyn(strv);
    }
    if ($('input[name=' + strv + '.changePermAllowed]').is(":checked")) {
        keepViewAllowedTrueForDyn(strv);
    }
}
;

function keepViewAllowedTrueForDyn(name) {
    $('input[name=' + name + '.viewAllowed]').attr('checked', true);
}
;


$(document).ready(function () {
    $('div #add_var_div_id a').live('click', function () {
        var rowIndex = $("#search_tab > tbody > tr").length;
        //	alert("add variable clicked tr size: " + rowIndex);
        $('#search_tab > tbody:last').append("<tr><td align='right'>Variable Name</td>\n" +
            "<td align='left'> \n" +
            "<input type='text' name='searchBean.varBeans[" + rowIndex + "].varName' value='' id='search_jspx_searchBean_varBeans_" + rowIndex + "__varName'/>\n" +
            "</td><td>" +

            "<table class='search_inner_tab' id='searchBean.varBeans[" + rowIndex + "]'>\n" +
            "<thead>\n" +
            "<tr>\n" +
            "<td colspan='4' class='lightbg_td'>\n" +
            "<div class='search_inline_div' id='add_att_div_id'>" +
            "More <b>attributes</b> search conditions &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
            "<a href='#' id='" + rowIndex + "'> <b>Add</b> <img src='/ands/images/add.gif' align='top' border='0'/></a>" +
            "</div>\n</td>\n</tr>\n</thead>\n" +
            "<tbody>\n" +
            "<tr>\n" +
            "<td align='right'>Attribute</td>\n" +
            "<td align='left'>	<input type='text' name='searchBean.varBeans[" + rowIndex + "].attBeans[0].attributeName' value='' id='search_jspx_searchBean_varBeans_" + rowIndex + "__attBeans_0__attributeName' /></td>\n" +
            "<td>\n" +
            "<select name='searchBean.varBeans[" + rowIndex + "].attBeans[0].comparison' id='search_jspx_searchBean_varBeans_" + rowIndex + "__attBeans_0__comparison' >\n" +
            "<option value='equals'>equals</option>\n" +
            "<option selected='selected' value='likes'>likes</option>\n" +
            "</select>" +
            "</td>\n" +
            "<td>\n" +
            "<input type='text' name='searchBean.varBeans[" + rowIndex + "].attBeans[0].value'  value=''  id='search_jspx_searchBean_varBeans_" + rowIndex + "__attBeans_0__value' />\n" +
            "</td>\n</tr>\n" +
            "</tbody></table></td>\n</tr>\n");
    })
});

$(document).ready(function () {
    $("div #add_att_div_id a").live('click', function () {
        var a_id = $(this).attr('id');
        //alert(" a id: " + a_id);
        // var rows = $(this).parent().parent().parent().parent().parent().children("tbody").children("tr").length;
        var var_tab_id = "searchBean.varBeans[" + a_id + "]";

        var rows = $(this).parents("table:first").children("tbody").children("tr").length;

        var findTbody = $(this).parents("table:first").children("tbody:last");
        $(findTbody).append("<tr>" +
            "<td align='right'>Attribute</td>\n" +
            "<td align='left'>	<input type='text' name='searchBean.varBeans[" + a_id + "].attBeans[" + rows + "].attributeName' value='' id='search_jspx_searchBean_varBeans_" + a_id + "__attBeans_" + rows + "__attributeName' /></td>\n" +
            "<td>\n" +
            "<select name='searchBean.varBeans[" + a_id + "].attBeans[" + rows + "].comparison' id='search_jspx_searchBean_varBeans_" + a_id + "__attBeans_" + rows + "__comparison' >\n" +
            "<option value='equals'>equals</option>\n" +
            "<option selected='selected' value='likes'>likes</option>\n" +
            "</select>" +
            "</td>\n" +
            "<td>\n" +
            "<input type='text' name='searchBean.varBeans[" + a_id + "].attBeans[" + rows + "].value'  value=''  id='search_jspx_searchBean_varBeans_" + a_id + "__attBeans_" + rows + "__value' />\n" +
            "</td>\n" +
            "</tr>\n");

    })
});


$("#permission_req input[type=checkbox]").live('click', function () {
    var act = $(this).attr('name');
    //alert(act);
    if ($(this).is(":checked")) {
        if (act != 'permReq.viewAllowed') {
            setViewPermTrueForPermReq();
        }
    }
});

function setViewPermTrueForPermReq() {
    $("input[name='permReq.viewAllowed']").attr('checked', true);
}
;

$("input[name='permReq.viewAllowed']").live('click', function () {
    if ($(this).is(":checked") == false) {
        if ($("input[name='permReq.editAllowed']").is(":checked")) {
            setViewPermTrueForPermReq();
        }
        if ($("input[name='permReq.importAllowed']").is(":checked")) {
            setViewPermTrueForPermReq();
        }
        if ($("input[name='permReq.exportAllowed']").is(":checked")) {
            setViewPermTrueForPermReq();
        }
        if ($("input[name='permReq.deleteAllowed']").is(":checked")) {
            setViewPermTrueForPermReq();
        }
        if ($("input[name='permReq.changePermAllowed']").is(":checked")) {
            setViewPermTrueForPermReq();
        }
    }
});

$(document).ready(function () {
    $("#ds_select_all").click(function () {
        var checked_status = this.checked;
        $("input[id=dataset_file]").each(function () {
            this.checked = checked_status;
        });
    });
});

$('input[name=extractRequired]').live('click', function () {
    var checked_status = this.checked;
    if (checked_status == false) {
        $("input[name=extractAllOrGlobal]").each(function () {
            this.checked = false;
        });
    } else {
        $("input[name=extractAllOrGlobal]").first().attr('checked', true);
    }
});

$('input[name=extractAllOrGlobal]').live('click', function () {
    var checked_status = this.checked;
    if (checked_status == true) {
        $("input[name=extractRequired]").attr('checked', true);
    }
});


$('#cancelLicence').live('click', function (e) {
    e.preventDefault();
    closePopupWindow();
});

$('#saveLicence').live('click', function (e) {
    e.preventDefault();
    var type = $('#plicence_type').val();
    var licence = $('#plicence_contents').val();

    if (licence == null || licence.trim() == "") {
        alert("The Licence must be provided!");
    } else {
        window.parent.$('#licence_type').val(type);
        window.parent.$('#licence_contents').val(licence);
        window.parent.$('.data_licence_div').text(licence);
        //remove the none licence div first
        removeNoneLicenceDiv();
        //then close the popup window
        closePopupWindow();
    }
});

$('#save_rm_party').live('click', function (e) {
    e.preventDefault();

    //fetch the party value from the popup windows
    var pKey = $('#ands_p_key').val();
    var pTitle = $('#ands_p_title').val();
    var pGivenName = $('#ands_p_givenname').val();
    var pSName = $('#ands_p_sname').val();
    var pEmail = $('#ands_p_email').val();
    var pAddress = $('#ands_p_address').val();
    var pUrl = $('#ands_p_url').val();
    var pDesc = $('#ands_p_desc').val();
    var pIdType = $('#ands_p_idtype').val();
    var pIdValue = $('#ands_p_idvalue').val();
    var pSrcType = $('#ands_p_srctype').val();
    var pSrcValue = $('#ands_p_srcvalue').val();
    var pGroupName = $('#ands_p_groupname').val();
    var pFromRm = $('#ands_p_fromrm').val();
    //try to find a party which already added in the form
    //then the party key will be used as identifier value to find a party which is already added
    var alreadyAddedPKey = window.parent.$("input[value=" + pKey + "]").val();
    //if this party is already added we have to update it
    if (alreadyAddedPKey != null) {
        var partyKeyName = window.parent.$("input[value=" + pKey + "]").attr('name');
        var startIndex = partyKeyName.indexOf("[") + 1;
        var endIndex = partyKeyName.indexOf("]");
        var keyIndex = partyKeyName.substring(startIndex, endIndex);
        //find the existed party row
        var foundTrRow = window.parent.$("#ands_party_div > tbody > tr:eq(" + keyIndex + ")");
        foundTrRow.empty();
        var innerHtml = "<td align='center' width='50'>\n<input id='mdRegForm_partyList_" + keyIndex + "__selected' class='check_box' type='checkbox' checked='checked' name='partyList[" + keyIndex + "].selected' value='true' />\n" +
            "</td>\n" +
            "<td>\n<div>" + pTitle + " " + pGivenName + " " + pSName + " ( " + pGroupName + " - " + pEmail + " )\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__partyKey' type='hidden' name='partyList[" + keyIndex + "].partyKey' value='" + pKey + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__personTitle' type='hidden' name='partyList[" + keyIndex + "].personTitle' value='" + pTitle + "'/>\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__personGivenName' type='hidden' name='partyList[" + keyIndex + "].personGivenName' value='" + pGivenName + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__personFamilyName' type='hidden' name='partyList[" + keyIndex + "].personFamilyName' value='" + pSName + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__email' type='hidden' name='partyList[" + keyIndex + "].email' value='" + pEmail + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__address' type='hidden' name='partyList[" + keyIndex + "].address' value='" + pAddress + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__url' type='hidden' name='partyList[" + keyIndex + "].url' value='" + pUrl + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__description' type='hidden' name='partyList[" + keyIndex + "].description' value='" + pDesc + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__identifierType' type='hidden' name='partyList[" + keyIndex + "].identifierType' value='" + pIdType + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__identifierValue' type='hidden' name='partyList[" + keyIndex + "].identifierValue' value='" + pIdValue + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__originateSourceType' type='hidden' name='partyList[" + keyIndex + "].originateSourceType' value='" + pSrcType + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__originateSourceValue' type='hidden' name='partyList[" + keyIndex + "].originateSourceValue' value='" + pSrcValue + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__groupName' type='hidden' name='partyList[" + keyIndex + "].groupName' value='" + pGroupName + "' />\n" +
            "<input id='mdRegForm_partyList_" + keyIndex + "__fromRm' type='hidden' name='partyList[" + keyIndex + "].fromRm' value='" + pFromRm + "' />\n" +
            "</div>\n</td>\n";
        foundTrRow.append(innerHtml);
    } else {
        if (window.parent.$('#ands_party_div > tbody').length == 0) {
            window.parent.$('#ands_party_div').append('<tbody />');
        }
        var rowIndex = window.parent.$("#ands_party_div > tbody > tr").length;

        var findTbody = window.parent.$('#ands_party_div > tbody:last');
        $(findTbody).append("<tr>\n<td align='center' width='50'>\n<input id='mdRegForm_partyList_" + rowIndex + "__selected' class='check_box' type='checkbox' checked='checked' name='partyList[" + rowIndex + "].selected' value='true' />\n" +
            "</td>\n" +
            "<td>\n<div>" + pTitle + " " + pGivenName + " " + pSName + " ( " + pGroupName + " - " + pEmail + " )\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__partyKey' type='hidden' name='partyList[" + rowIndex + "].partyKey' value='" + pKey + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__personTitle' type='hidden' name='partyList[" + rowIndex + "].personTitle' value='" + pTitle + "'/>\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__personGivenName' type='hidden' name='partyList[" + rowIndex + "].personGivenName' value='" + pGivenName + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__personFamilyName' type='hidden' name='partyList[" + rowIndex + "].personFamilyName' value='" + pSName + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__email' type='hidden' name='partyList[" + rowIndex + "].email' value='" + pEmail + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__address' type='hidden' name='partyList[" + rowIndex + "].address' value='" + pAddress + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__url' type='hidden' name='partyList[" + rowIndex + "].url' value='" + pUrl + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__description' type='hidden' name='partyList[" + rowIndex + "].description' value='" + pDesc + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__identifierType' type='hidden' name='partyList[" + rowIndex + "].identifierType' value='" + pIdType + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__identifierValue' type='hidden' name='partyList[" + rowIndex + "].identifierValue' value='" + pIdValue + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__originateSourceType' type='hidden' name='partyList[" + rowIndex + "].originateSourceType' value='" + pSrcType + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__originateSourceValue' type='hidden' name='partyList[" + rowIndex + "].originateSourceValue' value='" + pSrcValue + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__groupName' type='hidden' name='partyList[" + rowIndex + "].groupName' value='" + pGroupName + "' />\n" +
            "<input id='mdRegForm_partyList_" + rowIndex + "__fromRm' type='hidden' name='partyList[" + rowIndex + "].fromRm' value='" + pFromRm + "' />\n" +
            "</div>\n</td>\n</tr>\n"
        );
        removeNonePartyDiv();
    }
    //close the Popup windows
    closePopupWindow();
});

function closePopupWindow() {
    window.parent.$.superbox.close();
}

function removeNonePartyDiv() {
    var pnf = window.parent.$(".none_party_div");
    if (pnf != null) {
        window.parent.$('.none_party_div').remove();
    }
}


function removeNoneLicenceDiv() {
    var pnf = window.parent.$(".none_licence_div");
    if (pnf != null) {
        window.parent.$('.none_licence_div').remove();
    }
}
 