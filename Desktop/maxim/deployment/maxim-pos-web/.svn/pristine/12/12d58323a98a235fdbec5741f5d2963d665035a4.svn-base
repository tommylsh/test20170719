/**
 * Created by Gavin on 2016/01/08.
 */
$(function() {

    $(':input.form-control').closest('.form-group, [class*="col-"]').addClass('form-validator');

    $.extend($.fn.bootstrapValidator.DEFAULT_OPTIONS, {
        group: '.form-validator',                   // '.form-group'
        container: null,                            // ['tooltip', 'popover']
        excluded: [':disabled', ':hidden', ':not(:visible)'],
        message: 'The field is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        }
    });

    $.fn.select2.defaults.set('width', '100%');
    $.fn.select2.defaults.set('tags', false);
    $.fn.select2.defaults.set('multiple', false);
    $.fn.select2.defaults.set('allowClear', true);
    // $.fn.select2.defaults.set('selectOnClose', true);
    // $.fn.select2.defaults.set('minimumInputLength', 0);
    $.fn.select2.defaults.set('theme', 'bootstrap');
    $.fn.select2.defaults.set('language', getLanguageCookie());
    $.fn.select2.defaults.set('placeholder', $.i18n.prop('msg.selectPlaceholder'));

    $.extend($.noty.defaults, {
        layout        :  'bottomRight',  // top(Left|Center|Right) center(Left|Right) bottom(Left|Center|Right)
        theme         :  'relax',        // defaultTheme|bootstrapTheme|relax
        type          :  'success',      // alert|success|error|warning|information|confirmation
        text          :  '',             // can be html or string
        dismissQueue  :  true,           // If you want to use queue feature set this true
        timeout       :  false,          // delay for closing event. Set false for sticky notifications
        force         :  false,          // adds notification to the beginning of queue when set to true
        modal         :  false,          // modal dialog
        maxVisible    :  5,              // you can set max visible notification for dismissQueue true option,
        killer        :  true,           // for close all notifications before show
        closeWith     :  ['click'],      // ['click', 'button', 'hover', 'backdrop'] // backdrop click will close all notifications
        buttons       :  false           // an array of buttons
    });
    
    $.extend($.fn.dataTable.defaults, {
        select         :  true,
        serverSide     :  false,
        paging         :  true,
        pagingType     :  'full_numbers',
        lengthMenu     :  [10, 20, 50, 100, 200, 500],
        displayStart   :  0,
        displayLength  :  10,
        ordering       :  true,
        searching      :  true,
        processing     :  true,
        deferRender    :  true,
        lengthChange   :  true,
        language       :  {url : globalConfig.dtLangUrl}
    });

    $.extend($.fancybox.defaults, {
        width       : 800,
        height      : 600,
        minWidth    : 100,
        minHeight   : 100,
        maxWidth    : 9999,
        maxHeight   : 9999,
        autoSize	: false,
        fitToView	: false,
        closeClick	: false,
        openEffect	: 'none',
        closeEffect	: 'none',
        beforeLoad  : $.noop(),
        afterClose  : $.noop()
    });

});
