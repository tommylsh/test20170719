/**
 * Created by Gavin on 2016/01/08.
 */

$(function() {

    $('.select2').select2();

    $('.select').select2Remote({
    	url: '${basePath}/vta/select2!selectSysCode.action'
    });

    $('#vehModel').select2Remote({
        url: '${basePath}/vta/select2!selectVehModel.action',
        data: function(params) {
            var query = {vehBrandCode : $('#vehBrandCode').val()};
            if(!$.isEmpty(params)) {
                query.brandModel = '%'+params.term+'%';
            }
            return query;
        }
    });

    $('#vehModelYear').select2Remote({
        url: '${basePath}/vta/select2!selectVehModelYear.action',
        data: function(params) {
            var query = {
            	vehBrandCode : $('#vehBrandCode').val(),
            	vehModel : $('#vehModel').val()
            };
            if(!$.isEmpty(params)) {
                query.vehModelYear = '%'+params.term+'%';
            }
            return query;
        }
    });

    $('#vtaCompCode').select2Remote({
         url: '${basePath}/vta/select2!selectCompany.action'
    });
    
    $('#vehBrandCode').on('change', function() {
        var $vehModel = $('#vehModel');
        $vehModel.select2('val', '');
        $vehModel.change();
    });
    
    $('#vehModel').on('change', function() {
        var $vehModelYear = $('#vehModelYear');
        $vehModelYear.select2('val', '');
        $vehModelYear.change();
    });
    
    $('#vehmodelYear').on('change', $.noop());
	
});