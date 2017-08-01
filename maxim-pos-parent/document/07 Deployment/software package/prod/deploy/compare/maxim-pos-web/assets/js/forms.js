/**
 * Created by Gavin on 2016/01/08.
 */
$(function() {

    // $('[data-toggle="tooltip"]').tooltip();

    $('body').tooltip({
        selector: '[data-toggle="tooltip"]',
        container: 'body'
    });

    $('[data-provide="datepicker"]').datetimepicker({
        format: 'DD/MM/YYYY'
    }).attr('placeholder', $.i18n.prop('msg.datePlaceholder'));

    $('[data-provide="datetimepicker"]').datetimepicker({
        format: 'DD/MM/YYYY hh:mm'
    }).attr('placeholder', $.i18n.prop('msg.datePlaceholder'));
    

    $(':input.form-control.required').each(function(){
        $('label.control-label[for="'+this.id+'"]').addClass('control-label-required');
    });

    $('a.fancybox').fancybox({
        afterLoad: function () {
            this.width = $(this.element).data('fancybox-width') || $.fancybox.defaults.width;
            this.height = $(this.element).data('fancybox-height') || $.fancybox.defaults.height;
        }
    });

    $(':reset').click(function() {
        $.initSelection('.select2-hidden-accessible');
    });

    $.fn.select2Remote = function (options) {
        var opts = $.extend({}, $.fn.select2Remote.defaults, options);
        return this.select2({
            ajax : {
                url : opts.url,
                data : opts.data,
                processResults : opts.processResults
            }
        });
    };

    $.fn.select2Remote.defaults = {
        url : '',
        data : function(params) {
            var query = {};
            var codeType = $(this).attr('data-codeType');
            if(codeType) {
                query.codeType = codeType;
            }
            if(!$.isEmpty(params)) {
                query.queryString = String.format('%{0}%', params.term);
            }
            return query;
        },
        processResults : function(data, params) {
            var result = {results: []};
            $.each(data.data, function(index, obj){
                result.results.push({id: obj.id, text: obj.text});
            });
            return result;
        }
    };

    $.extend({
        initSelection: function (selector, text, value) {
            text = text || $.fn.select2.defaults.placeholder;
            value = value || '';
            var option = new Option(text, value);
            $(option).attr('selected', true);
            var $selector = $(selector);
            $selector.empty();
            $selector.append(option);
            $selector.trigger('change');
        },
        isEmpty : function (obj) {
            for (var name in obj) {
                return false;
            }
            return true;
        },
        formValidator : function (form) {
            var $validator = $(form).data('bootstrapValidator');
            if(!$validator) {
                return false;
            }
            var isValid = $validator.validate().isValid();
            if (!isValid) {
                $('.has-error:first :input').focus();
            }
            return isValid;
        },
        showResult : function (result) {
            if (result['success']) {
                $.message(String.format(
                    '<p><strong>{0}</strong></p>',
                    $.i18n.prop('msg.tip', $.i18n.prop('msg.operateSuccess'))
                ), true);
            } else {
                $.message(String.format(
                    '<p><strong>{0}</strong></p><p>{1}</p><p>{2}</p>',
                    $.i18n.prop('msg.tip', $.i18n.prop('msg.operateFailed')),
                    $.i18n.prop('msg.errorCode', result['errorCode']),
                    $.i18n.prop('msg.errorMsg', result['msg'])
                ), false);
            }
        },
        alert : function(msg, msgType, callback) {
            var title = $.i18n.prop('msg.tipTitle');
            var icon = '';
            var text = '';
            switch(msgType) {
                case 'success':
                    icon = '<i class="icon-ok icon-2x"></i>';
                    title = $.i18n.prop('msg.tipSuccess');
                    break;
                case 'error':
                    icon = '<i class="icon-remove icon-2x"></i>';
                    title = $.i18n.prop('msg.tipError');
                    break;
                case 'warn':
                    icon = '<i class="icon-warning-sign icon-2x"></i>';
                    title = $.i18n.prop('msg.tipWarn');
                    break;
                case 'confirm':
                    icon = '<i class="icon-question-sign icon-2x"></i>';
                    title = $.i18n.prop('msg.tipConfirm');
                    break;
                default:
                    break;
            }
            if(msgType) {
                text = String.format('{0}<strong class="h4">{1}：{2}</strong>', icon, title, msg);
            } else {
                text = String.format('<strong class="h4">{0}：{1}</strong>', title, msg);
            }
            noty({
                text: text,
                type: 'alert',
                layout: 'topCenter',
                modal: true,
                buttons: [{
                    addClass: 'btn btn-default btn-sm',
                    text: $.i18n.prop('msg.btnSure'),
                    onClick: function(noty) {
                        noty.close();
                        if(typeof callback === 'function') {
                            callback();
                        }
                    }
                }]
            });
        },
        confirm : function(msg, callback, options) {
            var icon = '<i class="icon-question-sign icon-2x"></i>';
            var title = options && options['title'] ? options['title'] : $.i18n.prop('msg.tipConfirm');
            var text = String.format('{0}<strong class="h4">{1}：{2}</strong>', icon, title, msg);
            noty({
                text: text,
                type: 'alert',
                layout: 'topCenter',
                modal: true,
                buttons: [{
                    addClass: 'btn btn-primary btn-sm',
                    text: options && options['buttonOk'] ? options['buttonOk'] : $.i18n.prop('msg.btnSure'),
                    onClick: function(noty) {
                        noty.close();
                        if(typeof callback === 'function') {
                            callback(true);
                        }
                    }
                },{
                    addClass: 'btn btn-default btn-sm',
                    text: options && options['buttonCancel'] ? options['buttonCancel'] : $.i18n.prop('msg.btnCancel'),
                    onClick: function(noty) {
                        noty.close();
                        if(typeof callback === 'function') {
                            callback(false);
                        }
                    }
                }]
            });
        },
        message : function(msg, success) {
            if(typeof success !== 'boolean') {
                success = true;
            }
            if(success) {
                noty({
                    text: msg,
                    type: 'success',
                    layout: 'bottomRight',
                    modal: false,
                    timeout: 0
                });
            } else {
                noty({
                    text: msg,
                    type: 'error',
                    layout: 'bottomRight',
                    modal: false,
                    timeout: 0
                });
            }
        }
    });

});