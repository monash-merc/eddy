<@s.if test="%{searched == true}">
    <div class="content_none_border_div">
        <div class="content_title">Search Results</div>
    </div>

    <div class="none_border_block">
            <span class="name_title">
                <@s.if test="%{searchBean.collectionOnly == true}">
                    Found a total of <font color="green"> ${pagination.totalRecords} </font> collection(s)  in this repository
                </@s.if>
                <@s.else>
                    Found a total of <font color="green"> ${dsPagination.totalRecords} </font> dataset(s)  in this repository
                </@s.else>

            </span>
    </div>

    <div class="msg_content">
        <@s.if test="%{searchBean.collectionOnly == true}">
            <a href="${base}/${pageLink}${pageSuffix}<@s.property value='pagination.pageNo' />" class="page_url"></a>
        </@s.if>
        <@s.else>
            <a href="${base}/${pageLink}${pageSuffix}<@s.property value='dsPagination.pageNo' />" class="page_url"></a>
        </@s.else>
    </div>
    <div class="content_none_border_div">
        <div class="none_border_block">
            <span class="filter_inline_span">
                Page size: <@s.select id="item_select_size" name="sizePerPage" headerKey="<@s.property value='sizePerPage' />"  list="pageSizeMap" cssClass="input_select_small" />
                &nbsp;Sorted by: <@s.select id="item_select_order" name="orderBy" headerKey="${orderBy}"  list="orderByMap" cssClass="input_select_small" />
                &nbsp;Ordered by: <@s.select id="item_select_otype" name="orderByType" headerKey="${orderByType}"  list="orderByTypeMap" cssClass="input_select_small" />
            </span>
        </div>
    </div>

    <@s.if test="%{searchBean.collectionOnly == true}">
        <@s.iterator status="colStat" value="pagination.pageResults" id="colResult" >
        <div class="data_display_div">
            <div class="data_title">
                <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id' />&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}"/><@s.property value="#colResult.name" /></a>
            </div>
            <div class="data_desc_div">
                <@s.property value="#colResult.briefDesc" />
            </div>
            <div class="data_other_info">
                    <span class="span_inline1">
                        Created by <@s.property value="#colResult.owner.displayName" />,
                    </span>
                    <span class="span_inline1">
                        Creation date: <@s.date name="#colResult.createdTime" format="yyyy-MM-dd hh:mm" />,
                    </span>
                   <span class="span_inline1">
                        Modified by <@s.property value="#colResult.modifiedByUser.displayName" />,
                    </span>
                    <span class="span_inline1">
                        Modified date: <@s.date name="#colResult.modifiedTime" format="yyyy-MM-dd hh:mm" />
                    </span>
            </div>
            <@s.if test="%{#colResult.funded == true}">
                <div class="data_tern_div">
                    [ <a href="http://www.tern.org.au" target="_blank">TERN-Funded</a> ]
                </div>
            </@s.if>
            <div class="data_action_link">
                <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#colResult.id' />&collection.owner.id=<@s.property value='#colResult.owner.id' />&viewType=${viewType}">View
                    details</a>
            </div>
            <div style="clear: both;"></div>
        </div>
        </@s.iterator>
    <div class="content_none_border_div">
        <div class="blank_separator"></div>
        <#include "../pagination/pag_style.ftl" />
    </div>
    </@s.if>

    <@s.else>
        <@s.iterator status="dsStat" value="dsPagination.pageResults" id="dsResult" >
        <div class="data_display_div">
            <div class="data_title">
                <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#dsResult.collection.id' />&collection.owner.id=<@s.property value='#dsResult.collection.owner.id' />&viewType=${viewType}"><@s.property value="#dsResult.collection.name" /></a>
            </div>
            <div class="data_desc_div">
                <@s.property value="#dsResult.collection.briefDesc" />
            </div>
            <div class="data_other_info">
                    <span class="span_inline1">
                        Created by <@s.property value="#dsResult.collection.owner.displayName" />,
                    </span>
                    <span class="span_inline1">
                        Creation date: <@s.date name="#dsResult.collection.createdTime" format="yyyy-MM-dd" />,
                    </span>
                   <span class="span_inline1">
                        Modified by <@s.property value="#dsResult.collection.modifiedByUser.displayName" />,
                    </span>
                    <span class="span_inline1">
                        Modified date: <@s.date name="#dsResult.collection.modifiedTime" format="yyyy-MM-dd" />
                    </span>
            </div>
            <div class="data_title">
                <a href="${base}/${viewColDetailLink}?collection.id=<@s.property value='#dsResult.collection.id' />&collection.owner.id=<@s.property value='#dsResult.collection.owner.id' />&viewType=${viewType}"><@s.property value="#dsResult.name" /></a>
            </div>
            <div style="clear: both;"></div>
        </div>
        </@s.iterator>
    <div class="content_none_border_div">
        <div class="blank_separator"></div>
        <#include "../pagination/search_page_style.ftl" />
    </div>
    </@s.else>
</@s.if>
