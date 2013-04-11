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

$(document).ready(function () {
    $("#add_permission").click(function () {
        var value_index = $("#selected_username").val();
        var selectedText = $('#selected_username option:selected').text();
        var rowIndex = $("#user_permissions > tbody > tr").length;

        //if an user permissions already added, just return.
        var el = $("input[id='user_id'][value='" + value_index + "']").val();
        if (el != null) {
            return;
        }
        var allRegExportAllowed = $("input[name='allRegUserPerm.exportAllowed']").is(":checked");
        if (value_index != '-1') {
            var permHtml = "<tr>";
            permHtml += "<td>" + selectedText;
            permHtml += "<input type='hidden' name='regUserPerms[" + rowIndex + "].id' value='0' id='regUserPerms" + rowIndex + "__id'/>";
            permHtml += "<input type='hidden' name='regUserPerms[" + rowIndex + "].uid' value='" + value_index + "' id='user_id'/></td>";
            permHtml += "<input type='hidden' name='regUserPerms[" + rowIndex + "].userName' value='" + selectedText + "' id='regUserPerms" + rowIndex + "__userName'/></td>";
            permHtml += "<td><input type='hidden' name='regUserPerms[" + rowIndex + "].viewAllowed' value='true' id='regUserPerms" + rowIndex + "__viewAllowed'/><input type='checkbox' class='check_box' name='displayViewAllowed' cssClass='check_box' disabled ='true' checked='checked'/></td>";
            if (allRegExportAllowed) {
                permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].exportAllowed' value='true' checked='checked' id='regUserPerms" + rowIndex + "__exportAllowed'/></td>";
            } else {
                permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].exportAllowed' value='true' id='regUserPerms" + rowIndex + "__exportAllowed'/></td>";
            }
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].importAllowed' value='true' id='regUserPerms" + rowIndex + "__importAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].racAllowed' value='true' id='regUserPerms" + rowIndex + "__racAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].updateAllowed' value='true' id='regUserPerms" + rowIndex + "__updateAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].deleteAllowed' value='true' id='regUserPerms" + rowIndex + "__deleteAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].acAllowed' value='true' id='regUserPerms" + rowIndex + "__acAllowed'/></td>";
            permHtml += "</tr>";

            $('#user_permissions > tbody:last').append(permHtml);
        }
    });
});


function setAllRegUserExportTrue() {
    $("input[name='allRegUserPerm.exportAllowed']").attr('checked', true);
}

function setIndividualExportTrue() {
    var rowIndex = $("#user_permissions > tbody > tr").length;
    for (var i = 0; i < rowIndex; i++) {
        ($("input[name='regUserPerms[" + i + "].exportAllowed']").attr('checked', true));
    }
}

$("#user_permissions input[type=checkbox]").live('click', function () {
        var act = $(this).attr('name');
        var checkBoxNamePreFix = act.substring(0, act.indexOf('.'));
        if ($(this).is(":checked")) {
            if (act == 'anonymousePerm.exportAllowed') {
                setAllRegUserExportTrue()
                setIndividualExportTrue();
            }
            if (act == 'allRegUserPerm.exportAllowed') {
                setIndividualExportTrue();
            }

            //check the cascading permissions
            //if an individual user rac allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.racAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
            }
            //if an individual user update allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.updateAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".racAllowed']").attr("checked", true);
            }

            //if an individual user delete allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.deleteAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".racAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".updateAllowed']").attr("checked", true);
            }

            //if an individual user access control allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.acAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".racAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".updateAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".deleteAllowed']").attr("checked", true);
            }
        } else {
            var anonyExportAllowed = $("input[name='anonymousePerm.exportAllowed']").is(":checked");
            var allRegExportAllowed = $("input[name='allRegUserPerm.exportAllowed']").is(":checked");

            //if allRegUser exportAllowed unckecked,
            if (act == 'allRegUserPerm.exportAllowed') {
                if (anonyExportAllowed) {
                    $(this).attr('checked', true);
                }
            }
            //The following will check the cascading permissions
            //if an individual user exportAllowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.exportAllowed') != -1)) {
                if (allRegExportAllowed) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".acAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".deleteAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".updateAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".racAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
            }

            //if an individual user import allowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.importAllowed') != -1)) {
                if ($("input[name='" + checkBoxNamePreFix + ".acAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".deleteAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".updateAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".racAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
            }

            //if an individual user rac allowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.racAllowed') != -1)) {
                if ($("input[name='" + checkBoxNamePreFix + ".acAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".deleteAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".updateAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
            }
            //if an individual user update allowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.updateAllowed') != -1)) {
                if ($("input[name='" + checkBoxNamePreFix + ".acAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
                if ($("input[name='" + checkBoxNamePreFix + ".deleteAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
            }

            //if an individual user delete allowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.deleteAllowed') != -1)) {
                if ($("input[name='" + checkBoxNamePreFix + ".acAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }
            }
        }
    }
)



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
        var licenceHtml = licence;
        if (type == 'tern') {
            licenceHtml = "<a href='" + licence + "' target='_blank'>" + licence + "</a>"
        }
        window.parent.$('#licence_type').val(type);
        window.parent.$('#licence_contents').val(licence);
        window.parent.$('.data_licence_div').html(licenceHtml);
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
    var pGroupKey = $('#ands_p_groupkey').val();
    var pGroupName = $('#ands_p_groupname').val();
    var pFromRm = $('#ands_p_fromrm').val();
    //try to find a party which already added in the form
    //then the party key will be used as identifier value to find a party which is already added
    var alreadyAddedPKey = window.parent.$("input[value='" + pKey + "']").val();
    //if this party is already added we have to update it
    if (alreadyAddedPKey != null) {
        var partyKeyName = window.parent.$("input[value='" + pKey + "']").attr('name');
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
            "<input id='mdRegForm_partyList_" + keyIndex + "__groupKey' type='hidden' name='partyList[" + keyIndex + "].groupKey' value='" + pGroupKey + "' />\n" +
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
            "<input id='mdRegForm_partyList_" + rowIndex + "__groupKey' type='hidden' name='partyList[" + rowIndex + "].groupKey' value='" + pGroupKey + "' />\n" +
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
