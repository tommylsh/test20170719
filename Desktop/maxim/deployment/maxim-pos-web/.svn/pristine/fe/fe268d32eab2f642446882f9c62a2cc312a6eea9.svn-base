<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="basePath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><sitemesh:write property='title'/></title>
    <link rel="shortcut icon" href="${basePath}/favicon.ico">
    <link rel="stylesheet" href="${basePath}/assets/ui/Bootstrap-3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/font-awesome-3.2.1/css/font-awesome.min.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/BootstrapValidator-0.5.2/css/bootstrapValidator.min.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/DataTables-1.10.10/css/dataTables.bootstrap.min.css">
    <%--
    <link rel="stylesheet" href="${basePath}/assets/ui/DataTables-1.10.10/extensions/Editor/css/editor.bootstrap.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/DataTables-1.10.10/extensions/Select/css/select.bootstrap.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/DataTables-1.10.10/extensions/Buttons/css/buttons.bootstrap.css">
    --%>
    <link rel="stylesheet" href="${basePath}/assets/ui/jQuery-Select2-4.0.1/css/select2.min.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/jQuery-Select2-4.0.1/css/select2-bootstrap.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/Bootstrap-fileinput-4.3.1/css/fileinput.min.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/jquery-fancyBox-2.1.5/css/jquery.fancybox.css">
    <link rel="stylesheet" href="${basePath}/assets/ui/Bootstrap-datetimepicker-4.17.37/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="${basePath}/assets/css/master.css">
    <link rel="stylesheet" href="${basePath}/assets/css/base.css">
    <link rel="stylesheet" href="${basePath}/assets/css/forms.css">
    <link rel="stylesheet" href="${basePath}/assets/css/module.css">
    <script type="text/javascript" src="${basePath}/assets/ui/jQuery-2.1.4/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/Bootstrap-3.3.5/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/BootstrapValidator-0.5.2/js/bootstrapValidator.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/js/dataTables.bootstrap.min.js"></script>
    <%--
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/extensions/Editor/js/dataTables.editor.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/extensions/Editor/js/editor.bootstrap.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/extensions/Select/js/dataTables.select.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/extensions/Buttons/js/dataTables.buttons.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/DataTables-1.10.10/extensions/Buttons/js/buttons.bootstrap.js"></script>
    --%>
    <script type="text/javascript" src="${basePath}/assets/ui/jQuery-Select2-4.0.1/js/select2.full.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/jQuery-Noty-2.3.8/js/noty/packaged/jquery.noty.packaged.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/jsrender/jsrender.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/Bootstrap-fileinput-4.3.1/js/fileinput.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/jquery-fancyBox-2.1.5/js/jquery.fancybox.pack.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/moment-2.13.0/js/moment.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/Bootstrap-datetimepicker-4.17.37/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/jQuery-i18n-properties-1.2.2/js/jquery.i18n.properties.min.js"></script>
    <script type="text/javascript" src="${basePath}/assets/ui/BootstrapValidator-0.5.2/js/language/zh_TW.js"></script>
    <script type="text/javascript" src="${basePath}/assets/js/base.js"></script>
    <sitemesh:write property='head'/>
</head>
<body>
<sitemesh:write property='body'/>
<script type="text/javascript">
    var globalConfig = {dtLangUrl: '${basePath}/assets/ui/DataTables-1.10.10/js/i18n/zh_TW.json'};
    $(function () {
        $.i18n.properties({
            name: ['Messages', 'Forms'],
            path: '${basePath}/assets/i18n/',
            async: false,
            mode: 'both',
            encoding: 'UTF-8',
            language: getLanguageCookie(),
            checkAvailableLanguages: true,
            callback: function () {
                $('[data-i18n]').each(function () {
                    var text = $.i18n.prop($(this).data('i18n'));
                    $(this).append(text);
                    var $id = $(this).attr('for');
                    if ($id) {
                        var $input = $('#' + $id);
                        if ($input && !$input.attr('placeholder')) {
                            $input.attr('placeholder', text);
                        }
                    }
                });
            }
        });
    });
</script>
<script type="text/javascript" src="${basePath}/assets/js/master.js"></script>
<script type="text/javascript" src="${basePath}/assets/js/forms.js"></script>
<script type="text/javascript" src="${basePath}/assets/js/module.js"></script>
</body>
</html>