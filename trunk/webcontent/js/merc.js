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
        var allRegViewAllowed = $("input[name='allRegUserPerm.viewAllowed']").is(":checked");
        var allRegExportAllowed = $("input[name='allRegUserPerm.exportAllowed']").is(":checked");
        if (value_index != '-1') {
            var permHtml = "<tr>";
            permHtml += "<td>" + selectedText;
            permHtml += "<input type='hidden' name='regUserPerms[" + rowIndex + "].id' value='0' id='regUserPerms_id'/>";
            permHtml += "<input type='hidden' name='regUserPerms[" + rowIndex + "].uid' value='" + value_index + "' id='user_id'/>";
            permHtml += "<input type='hidden' name='regUserPerms[" + rowIndex + "].userName' value='" + selectedText + "' id='regUserPerms_userName'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].viewAllowed' value='true' checked='checked' id='regUserPerms_viewAllowed'/></td>";
            if (allRegExportAllowed) {
                permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].exportAllowed' value='true' checked='checked' id='regUserPerms_exportAllowed'/></td>";
            } else {
                permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].exportAllowed' value='true' id='regUserPerms_exportAllowed'/></td>";
            }
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].importAllowed' value='true' id='regUserPerms_importAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].racAllowed' value='true' id='regUserPerms_racAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].updateAllowed' value='true' id='regUserPerms_updateAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].deleteAllowed' value='true' id='regUserPerms_deleteAllowed'/></td>";
            permHtml += "<td><input type='checkbox' class='check_box' name='regUserPerms[" + rowIndex + "].acAllowed' value='true' id='regUserPerms_acAllowed'/></td>";
            permHtml += "<td align='center'><div class='remove_user_perm' title='remove this user permissions'>&nbsp;</div></td>";
            permHtml += "</tr>";

            $('#user_permissions > tbody:last').append(permHtml);
        }
    });
});


function setIndividualViewTrue() {
    var rowIndex = $("#user_permissions > tbody > tr").length;
    for (var i = 0; i < rowIndex; i++) {
        ($("input[name='regUserPerms[" + i + "].viewAllowed']").attr('checked', true));
    }
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

            if (act == 'anonymousePerm.viewAllowed') {
                $("input[name='allRegUserPerm.viewAllowed']").attr('checked', true);
                setIndividualViewTrue();
            }
            if (act == 'allRegUserPerm.viewAllowed') {
                setIndividualViewTrue();
            }

            if (act == 'anonymousePerm.exportAllowed') {
                $("input[name='anonymousePerm.viewAllowed']").attr('checked', true);
                $("input[name='allRegUserPerm.viewAllowed']").attr('checked', true);
                $("input[name='allRegUserPerm.exportAllowed']").attr('checked', true);
                setIndividualViewTrue()
                setIndividualExportTrue();
            }

            if (act == 'allRegUserPerm.exportAllowed') {
                $("input[name='allRegUserPerm.viewAllowed']").attr('checked', true);
                setIndividualViewTrue()
                setIndividualExportTrue();
            }

            //check the cascading permissions

            //if an individual user export allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.exportAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".viewAllowed']").attr("checked", true);
            }
            //if an individual user import allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.importAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".viewAllowed']").attr("checked", true);
            }

            //if an individual user rac allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.racAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".viewAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
            }
            //if an individual user update allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.updateAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".viewAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".racAllowed']").attr("checked", true);
            }

            //if an individual user delete allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.deleteAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".viewAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".racAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".updateAllowed']").attr("checked", true);
            }

            //if an individual user access control allowed checked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.acAllowed') != -1)) {
                $("input[name='" + checkBoxNamePreFix + ".viewAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".exportAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".importAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".racAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".updateAllowed']").attr("checked", true);
                $("input[name='" + checkBoxNamePreFix + ".deleteAllowed']").attr("checked", true);
            }
        } else {
            var anonyViewAllowed = $("input[name='anonymousePerm.viewAllowed']").is(":checked");
            var anonyExportAllowed = $("input[name='anonymousePerm.exportAllowed']").is(":checked");
            var allRegViewAllowed = $("input[name='allRegUserPerm.viewAllowed']").is(":checked");
            var allRegExportAllowed = $("input[name='allRegUserPerm.exportAllowed']").is(":checked");
            //if anonymous viewAllowed unckecked,
            if (act == 'anonymousePerm.viewAllowed') {
                $(this).attr('checked', true);
                if (anonyExportAllowed) {
                    $(this).attr('checked', true);
                }
            }
            //if allRegUser viewAllowed unckecked,
            if (act == 'allRegUserPerm.viewAllowed') {
                if (anonyViewAllowed || anonyExportAllowed || allRegExportAllowed) {
                    $(this).attr('checked', true);
                }
            }

            //if allRegUser exportAllowed unckecked,
            if (act == 'allRegUserPerm.exportAllowed') {
                if (anonyExportAllowed) {
                    $(this).attr('checked', true);
                }
            }
            //The following will check the cascading permissions
            //if an individual user viewAllowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.viewAllowed') != -1)) {

                if (anonyViewAllowed || anonyExportAllowed || allRegViewAllowed || allRegExportAllowed) {
                    $(this).attr('checked', true);
                }

                if ($("input[name='" + checkBoxNamePreFix + ".exportAllowed']").is(":checked")) {
                    $(this).attr('checked', true);
                }

                if ($("input[name='" + checkBoxNamePreFix + ".importAllowed']").is(":checked")) {
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

            //if an individual user exportAllowed unchecked
            if ((act.indexOf('regUserPerms') != -1) && (act.indexOf('.exportAllowed') != -1)) {
                if (anonyExportAllowed || allRegExportAllowed) {
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


$('.remove_user_perm').live('click', function (event) {
    event.preventDefault();
    var trRowId = $(this).closest('tr');
    var trId = trRowId.attr('id');
    trRowId.remove();
    resortPermissionTabIndex();
})

function resortPermissionTabIndex() {
    var index = 0;

    $('#user_permissions > tbody > tr').each(function () {
        //permission id
        var permId = $(this).find('#regUserPerms_id');
        permId.attr('name', 'regUserPerms[' + index + '].id');

        //perm user id
        var permUserId = $(this).find('#user_id');
        permUserId.attr('name', 'regUserPerms[' + index + '].uid');

        //perm user name
        var permUserName = $(this).find('#regUserPerms_userName');
        permUserName.attr('name', 'regUserPerms[' + index + '].userName');

        //perm view allowed
        var permViewAllowed = $(this).find('#regUserPerms_viewAllowed');
        permViewAllowed.attr('name', 'regUserPerms[' + index + '].viewAllowed');
        //_perm view allowed
        var _permViewAllowed = $(this).find('#__checkbox_regUserPerms_viewAllowed');
        if (_permViewAllowed != null && _permViewAllowed != 'undefined') {
            _permViewAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].viewAllowed');
        }

        //perm export allowed
        var permExportAllowed = $(this).find('#regUserPerms_exportAllowed');
        permExportAllowed.attr('name', 'regUserPerms[' + index + '].exportAllowed');
        //_perm export allowed
        var _permExportAllowed = $(this).find('#__checkbox_regUserPerms_exportAllowed');
        if (_permExportAllowed != null && _permExportAllowed != 'undefined') {
            _permExportAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].exportAllowed');
        }

        //perm import allowed
        var permImportAllowed = $(this).find('#regUserPerms_importAllowed');
        permImportAllowed.attr('name', 'regUserPerms[' + index + '].importAllowed');
        //_perm import allowed
        var _permImportAllowed = $(this).find('#__checkbox_regUserPerms_importAllowed');
        if (_permImportAllowed != null && _permImportAllowed != 'undefined') {
            _permImportAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].importAllowed');
        }

        //perm rac allowed
        var permRacAllowed = $(this).find('#regUserPerms_racAllowed');
        permRacAllowed.attr('name', 'regUserPerms[' + index + '].racAllowed');
        //_perm rac allowed
        var _permRacAllowed = $(this).find('#__checkbox_regUserPerms_racAllowed');
        if (_permRacAllowed != null && _permRacAllowed != 'undefined') {
            _permRacAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].racAllowed');
        }

        //perm update allowed
        var permUpdateAllowed = $(this).find('#regUserPerms_updateAllowed');
        permUpdateAllowed.attr('name', 'regUserPerms[' + index + '].updateAllowed');
        //_perm update allowed
        var _permUpdateAllowed = $(this).find('#__checkbox_regUserPerms_updateAllowed');
        if (_permUpdateAllowed != null && _permUpdateAllowed != 'undefined') {
            _permUpdateAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].updateAllowed');
        }

        //perm delete allowed
        var permDeleteAllowed = $(this).find('#regUserPerms_deleteAllowed');
        permDeleteAllowed.attr('name', 'regUserPerms[' + index + '].deleteAllowed');
        //_perm delete allowed
        var _permDeleteAllowed = $(this).find('#__checkbox_regUserPerms_deleteAllowed');
        if (_permDeleteAllowed != null && _permDeleteAllowed != 'undefined') {
            _permDeleteAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].deleteAllowed');
        }

        //perm ac allowed
        var permAcAllowed = $(this).find('#regUserPerms_acAllowed');
        permAcAllowed.attr('name', 'regUserPerms[' + index + '].acAllowed');
        //_perm ac allowed
        var _permAcAllowed = $(this).find('#__checkbox_regUserPerms_acAllowed');
        if (_permAcAllowed != null && _permAcAllowed != 'undefined') {
            _permAcAllowed.attr('name', '__checkbox_regUserPerms[' + index + '].acAllowed');
        }

    });
}

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
        var values = $('#licence_form').serialize();
        $.ajax({
            url:'saveLicence.jspx',
            type:'post',
            dataType:'json',
            data:values,
            success:processLicenceResponse,
            error:displayLicenceExp
        })
    }
});

function processLicenceResponse(responseData) {
    var success = responseData.succeed;
    if (success) {
        var type = $('#plicence_type').val();
        var licence = $('#plicence_contents').val();
        window.parent.$('#licence_type').val(type);
        window.parent.$('#licence_contents').val(licence);
        var licenceHtml = licence;
        if (type == 'tern') {
            window.parent.$('.user_defined_licence').html('');
            window.parent.$('.tern_licence_hidden').show();
            window.parent.$('.tern_licence').show();
        } else {
            window.parent.$('.tern_licence').hide();
            window.parent.$('.tern_licence_hidden').hide();
            window.parent.$('.user_defined_licence').html(licenceHtml);
        }
        removeNoneLicenceDiv();
        //then close the popup window
        closePopupWindow();
    } else {
        displayErrorDiv(responseData.msg)
    }
}

function displayLicenceExp(jqXHR, textStatus, errorThrown) {
    displayErrorDiv("Failed to save the licence, Please close the popup window and try it again.");
}
function displayErrorDiv(errorMsg) {
    var error_section = $('.error_msg_section');
    var errorhtml = "<div class='fieldError'><ul class='errorMessage'><li><span>";
    errorhtml += errorMsg;
    errorhtml += "</span></li></ul></div>";
    error_section.html(errorhtml);
    error_section.show();
}

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


//Restricted Access control
$("#ra_enabled").live('click', function () {
    var im_ra_setting = $(".im_ra_section");
    if ($(this).is(":checked")) {
        im_ra_setting.show();
    } else {
        im_ra_setting.hide();
    }
});


$(".dataset_info").live('click', function () {
    //grey the previous ra control setting
    $('.ra_control2').attr('class', 'ra_control1');
    //remove the previous highlight dataset info
    $('#selected_ds').removeAttr('id');

    //highlight the current dataset info
    $(this).attr('id', 'selected_ds');
    //highlight the current ra control setting
    var racontrol = $(this).find('.ra_control1');
    if (racontrol != null || racontrol != 'undefined') {
        racontrol.attr('class', 'ra_control2');
    }
});

//file restricted access setting
$(".ra_control1").live('click', function () {
    var id = $(this).attr('id');
    displayRASettings(id);
})


//file restricted access setting
$(".ra_control2").live('click', function () {
    var id = $(this).attr('id');
    displayRASettings(id);
})


//highlight the ra control settings
$(".ra_control1").live('mouseover', function () {
    $(this).attr('class', 'ra_control2');
})

//grey the ra control settings
$(".ra_control2").live('mouseout', function () {
    $(this).attr('class', 'ra_control1');
})

//popup the ra settings
function displayRASettings(rac_id) {
    //find any previous active ra settings window
    var previous_rac_div = $("div[active='true']");
    previous_rac_div.hide();
    //find current one
    var rac_div = $('#' + rac_id);
    var ds_ra_control_div = $('#ds_ra_' + rac_id);
    var rac_position = rac_div.position();
    var p_top = rac_position.top - 60;
    var p_left = rac_position.left - 310;
    ds_ra_control_div.attr('style', 'dispplay:block;position:absolute;z-index:2200;top:' + p_top + 'px;left:' + p_left + 'px;');

    ds_ra_control_div.show();
    ds_ra_control_div.attr('active', 'true');
    //highlight
    var ds_info = $('#ds_' + rac_id);
    ds_info.attr('id', 'selected_ds');

    //clean any previous message
    cleanPreviousRAMsg(rac_id);
}

//ra settings close
$("div.ds_ra_close").live('click', function (event) {
    var ds_ra_control_div = $(this).closest('.dataset_ra_section');
    ds_ra_control_div.hide();
})

var formIdIndex;
//setup restricted access ajax action
$('#setup_ra').live('click', function (e) {
    e.preventDefault();

    var setupRAForm = $(this).closest('form');
    var name = setupRAForm.attr('name');
    formIdIndex = name.substr(14, name.length);

    //clean previous msg
    cleanPreviousRAMsg(formIdIndex);

    var values = setupRAForm.serialize();
    $.ajax({
        url:'rasetup.jspx',
        type:'post',
        dataType:'json',
        data:values,
        success:processRAResponse,
        error:displayRASetupError
    })
})

function cleanPreviousRAMsg(idIndex) {
    var racSuccessMsgDiv = $('#rac_success_' + idIndex);
    var racErrorMsgDiv = $('#rac_error_' + idIndex);
    racSuccessMsgDiv.hide();
    racErrorMsgDiv.hide();
}

function processRAResponse(raResponse) {
    var succeed = raResponse.succeed;
    if (succeed) {
        raSuccessUpdate(raResponse.startDate, raResponse.endDate, raResponse.messages[0]);
    } else {
        raErrorMessage(raResponse.messages);
    }
}

//update the ra info after success
function raSuccessUpdate(startDate, endDate, message) {
    var racSuccessMsgDiv = $('#rac_success_' + formIdIndex);
    var racSuccessMsg = racSuccessMsgDiv.find('.rac_success_msg');
    racSuccessMsg.html(message);
    racSuccessMsgDiv.show();

    //update the starting date and ra info
    var hiddenStartDate = $('#start_date_' + formIdIndex);
    hiddenStartDate.attr('value', startDate);
    var endDateInput = $('#end_date_' + formIdIndex);
    endDateInput.attr('value', endDate);
    var raInfoSpec = $('#ra_info_spec_' + formIdIndex);
    var htmlRaInfo = "Access to this file is restricted until " + endDate + ".";
    raInfoSpec.html(htmlRaInfo);
    var dataset_ra_section_div = $('#ds_ra_' + formIdIndex);
    //hidden the ra setting form
    dataset_ra_section_div.delay(1500).fadeOut(500);
}

function raErrorMessage(errorMsgs) {
    var errorHtml = "<ul>"
    $.each(errorMsgs, function (i, msg) {
        errorHtml += "<li>" + msg + " </li>"
    });
    errorHtml += "</ul>";
    var racErrorMsgDiv = $('#rac_error_' + formIdIndex);
    var racErrorItermDiv = racErrorMsgDiv.find('.rac_error_msg_item_div');
    racErrorItermDiv.empty();
    racErrorItermDiv.append(errorHtml);
    racErrorMsgDiv.show();
}

function displayRASetupError(jqXHR, textStatus, errorThrown) {
    raErrorMessage(["Failed to connect to the server, please refresh the page."]);
}

//Export dataset error message
$('#accept_licence').live('click', function () {
    if ($(this).is(":checked")) {
        $('.error_msg_section').hide();
    }
})





