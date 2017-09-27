$(document).ready(function() {

    console.log('hallo');

    var sentence = decodeURIComponent(getUrlParameter('sentence')).replace(/\+/g, ' ');
    console.log('sentence:' + sentence);
    if (sentence != 'undefined')
      $("#sentence").val(sentence);
    sentence = $("#sentence").val();

    var translation = decodeURIComponent(getUrlParameter('translation')).replace(/\+/g, ' ');
    console.log('translation:' + translation);
    if (translation != 'undefined')
        $("#translation").val(translation);
    translation = $("#translation").val();

    var input = decodeURIComponent(getUrlParameter('input')).replace(/\+/g, ' ');
    console.log('input:' + input);

    if (input == 'undefined') input = 'manual';
    $("#input").val(input);

//    if (input != 'undefined')
//        $('#input').selectpicker('val', input);
    input = $("#input").val();

    var tl = decodeURIComponent(getUrlParameter('tl')).replace(/\+/g, ' ');
    console.log('tl:' + tl);
    if (tl != 'undefined')
        $('#targetLanguage').selectpicker('val', tl);
    tl = $("#targetLanguage").val();

    var no = decodeURIComponent(getUrlParameter('no')).replace(/\+/g, ' ');
    if (no != 'undefined')
        $('#no').val(parseInt(no));
    console.log('no:' + no);
    no = $("#no").val();

    if (input == 'manual'){
                $('#no').css("visibility", "hidden");
                $('#arrows').css("visibility", "hidden");
           $('#targetLanguageDiv').css("visibility", "visible");
            }
            else {
                $('#no').css("visibility", "visible");
                $('#arrows').css("visibility", "visible");
           $('#targetLanguageDiv').css("visibility", "hidden");
            }
//     else {
//     }

    (function ($) {

        $('#targetLanguage').on('change', function(){

            console.log(input)

            console.log(input != 'manual')

            if ($('#input').val() != 'manual'){
                $.ajax({
                    type: 'GET',
                    url: "" + '/transfer/retrieve/' + encodeURIComponent(input) + "/" + no + "/" +  $('#targetLanguage').val(),
                    dataType: "json",
                    success: updateSentencePair
                });
            }
        });

        //on change function i need to control selected value
        $('#input').on('change', function(){
//            var input = $('#input').val();

            console.log(input)

            if (input == 'manual'){
                $('#no').css("visibility", "hidden");
                $('#arrows').css("visibility", "hidden");
            }
            else {
                $('#no').css("visibility", "visible");
                $('#arrows').css("visibility", "visible");
            $('.spinner input').val(0);
            }

            if (input != 'manual'){

                    var path = $( "input:file" ).val();
                    console.log(path);

                    $.ajax({
                        type: 'GET',
                        url: "" + '/transfer/retrieve/' + encodeURIComponent(input) + "/" + no + "/" +  $('#targetLanguage').val(),
                        dataType: "json",
                        success: updateSentencePair
                    });
                }
        });

        $('.spinner .btn:first-of-type').on('click', function() {
            no = parseInt($('.spinner input').val(), 10) + 1;
//            input = $('#input').val();
            $('.spinner input').val(no);
                            var path = $( "input:file" ).val();
                            console.log(path);

            if (input != 'manual'){
            $.ajax({
                type: 'GET',
                url: "" + '/transfer/retrieve/' + encodeURIComponent(input) + "/" + no + "/" +  $('#targetLanguage').val(),
                dataType: "json",
                success: updateSentencePair
            });
            }
        });

        $('.spinner .btn:last-of-type').on('click', function() {
            no = parseInt($('.spinner input').val(), 10) - 1;
            $('.spinner input').val(no);
            if (input != 'manual'){
            $.ajax({
                type: 'GET',
                url: "" + '/transfer/retrieve/' + encodeURIComponent(input) + "/" + no + "/" +  $('#targetLanguage').val(),
                dataType: "json",
                success: updateSentencePair
            });
            }
        });

    })(jQuery);

    if (input == 'manual' && translation != ''){
        $.ajax({
                type: 'GET',
                url: "" + '/transfer/manual/' + encodeURIComponent($('#sentence').val()) + "/" + tl + "/" + encodeURIComponent(translation),
                dataType: "json",
                success: visualizeAlignment
            });

          $('#no').css("visibility", "hidden");
          $('#arrows').css("visibility", "hidden");
    }

    if (input != 'manual' && translation != ''){
        $.ajax({
            type: 'GET',
            url: "" + '/transfer/corpus/' +
                encodeURIComponent(input) +
                "/" + no +
                "/" + tl +
                "/" + encodeURIComponent(sentence) +
                "/" + encodeURIComponent(translation),
            dataType: "json",
            success: visualizeAlignment
        });
        $('#no').css("visibility", "visible");
        $('#arrows').css("visibility", "visible");
    }
});


function query() {

    // get all input values
	var tl = $('#targetLanguage').val();

	var input = $('#input').val();

	var sentence = $('#sentence').val();
	var translation = $('#translation').val();
	var no = $('#no').val();

	var replace = "index.html?input=" + input + "&tl=" + tl + "&no=" + no ;

	if (sentence != '')  replace += "&sentence=" + encodeURIComponent(sentence);
	if (translation != '')  replace += "&translation=" + encodeURIComponent(translation);

    window.location.replace(replace);
}


function updateSentencePair(processedSentence) {
    console.log('updating');
    $("#sentence").val(replaceAll(processedSentence.sourceSentence, '   ', ' '));
    $("#translation").val(replaceAll(processedSentence.targetSentence, '   ', ' '));
    console.log(processedSentence.targetLanguage);

    $('select[name=targetLanguage]').val(processedSentence.targetLanguage);
    $('.selectpicker').selectpicker('refresh');
}

function escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}


function replaceAll(str, find, replace) {
  return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}


function visualizeAlignment(processedSentence){

    $("#sentence").val(replaceAll(processedSentence.sourceSentence, '   ', ' '));
    $("#translation").val(replaceAll(processedSentence.targetSentence, '   ', ' '));
    console.log(processedSentence);

    var text = replaceAll(processedSentence.sourceSentence, ' ', '\u00A0')  + '\u00A0';
    var hyp =  replaceAll(processedSentence.targetSentence, ' ', '\u00A0')   + '\u00A0';

    d3.select("#svg-text").text(text);
    d3.select("#svg-hyp").text(hyp);

    visualize(text, hyp, processedSentence);

    updateVisualization();
}

function visualize(text, hyp, processedSentence) {

    var alignments = processedSentence.alignments;

    var sourceTokens = processedSentence.tokensSL;
    var tokensTL_predicted = processedSentence.tokensTL_predicted;
    var tokensTL_projected = processedSentence.tokensTL_projected;

    var nerSL = processedSentence.nerSL;
    var nerTL_projected = processedSentence.nerTL_projected;

    var arcsSL = processedSentence.arcsSL;
    var arcsTL_projected = processedSentence.arcsTL_projected;
    var arcsTL_predicted = processedSentence.arcsTL_predicted;

    var framesSL = processedSentence.framesSL;
    var framesTL_predicted = processedSentence.framesTL_predicted;
    var framesTL_projected = processedSentence.framesTL_projected;


    var textY = 0;
    var hypY = 10;

    var sourceBaseY = 108;
    var targetBaseY = 325;

    var svg = d3.select("svg");

    function addEntityToMap(entities) {
        Array.from(entities).forEach(function (item) {
            console.log(item);
            if (entityToIdMap[item[0]] == null) {
                entityToIdMap[item[0]] = item[1];
            }
            else {
                console.error("ERROR: repetition of entity IDs! Potentially duplicate information! ");
            }
        });
    }

    function highlightSpans(entities, blockID, yCoordinate, type) {

            var adj = 12;

            entities.forEach(function(item, i) {
                var offset = document.getElementById(blockID).getStartPositionOfChar(Number(item[1][0])).x;
                var width = document.getElementById(blockID).getStartPositionOfChar(Number(item[1][1])).x - offset;

                var color = "blue";
                if (item[2] == 1) color = "red";
                if (item[2] == 2) color = "green";
                if (item[2] == 3) color = "orange";

                d3.select("svg").append("rect").attr("x", offset).
                                                attr("y", yCoordinate).
                                                attr("width", width).
                                                attr("height", (4*adj) + (item[2]*2*adj)).
                                                attr("ry", 5).
                                                attr("fill", "none").
                                                style("stroke-dasharray", ("3, 3")).
                                                attr("id", "yo").
                                                attr("class", "span_" + type)
                                                .attr("stroke-width", 1)
                                                .attr("stroke", color);

                var textX = offset + (width / 2);

                var adjustedY = yCoordinate + (4*adj) + (item[2]*2*adj) - 4;
                var myText =  d3.select("svg").append("text")
                    .attr("x", textX)
                    .attr("y", adjustedY)
                    .attr('fill', color)
                    .attr('text-anchor', 'middle')
                    .attr("class", "span_" + type)//easy to style with CSS
                    .text(item[0]);
            });
        }

    function highlightFrames(entities, blockID, yCoordinate, st) {

        var adj = 12;

        entities.forEach(function(item, i) {
            var offset = document.getElementById(blockID).getStartPositionOfChar(Number(item[1][0])).x;
            var width = document.getElementById(blockID).getStartPositionOfChar(Number(item[1][1])).x - offset;

            var color = "green";
            if (item[2] == 1) color = "red";
            if (item[2] == 2) color = "blue";

            d3.select("svg").append("rect").attr("x", offset).
                                            attr("y", yCoordinate).
                                            attr("width", width).
                                            attr("height", (4*adj) + (item[2]*2*adj)).
                                            attr("ry", 5).
                                            attr("fill", "none").
//                                            attr("fill", "yellow").
                                            style("stroke-dasharray", ("3, 3")).
                                            attr("id", "yo").
                                            attr("class", "frame_rect" + item[2] + "_" + st)
//                                            attr("opacity", 1)
                                            .attr("stroke-width", 1)
                                            .attr("stroke", color);

            var textX = offset + (width / 2);
//            if (item[0].includes('.')) {
//                 textX = 40;
//            }

            var adjustedY = yCoordinate + (4*adj) + (item[2]*2*adj) - 4;
            var myText =  d3.select("svg").append("text")
                .attr("x", textX)
                .attr("y", adjustedY)
                .attr('fill', color)
                .attr('text-anchor', 'middle')
                .attr("class", "frame_label_" + st)//easy to style with CSS
                .text(item[0]);
        });
    }

    function highlightTokens(entities, blockID, yCoordinate, st) {

        entities.forEach(function(item, i) {
            var offset = document.getElementById(blockID).getStartPositionOfChar(Number(item[1][0])).x;
            var width = document.getElementById(blockID).getStartPositionOfChar(Number(item[1][1])).x - offset;
            d3.select("svg").append("rect").attr("x", offset).
                                            attr("y", yCoordinate).
                                            attr("width", width).
                                            attr("height", 22).
                                            attr("ry", 5).
                                            attr("fill", "none").
                                            attr("id", "yo").
                                            attr("class", "token_rect").
                                            attr("opacity", 0.3).attr("stroke-width", 0.5).attr("stroke", "none");

        var myText =  d3.select("svg").append("text")
                        .attr("x", offset + (width / 2))
                        .attr("y", yCoordinate + 27)
                        .attr('text-anchor', 'middle')
                        .attr("class", "pos_" + st)//easy to style with CSS
                        .text(item[2]);
        });
    }

    highlightTokens(sourceTokens, "svg-text", sourceBaseY, "source");

    highlightTokens(tokensTL_predicted, "svg-hyp", targetBaseY, "predicted");
    highlightTokens(tokensTL_projected, "svg-hyp", targetBaseY, "projected");

    // create a map from entity to entity-id
    var entityToIdMap = {};
    addEntityToMap(sourceTokens);
    addEntityToMap(tokensTL_predicted);

    // get x-y coordinates for edge start-ends
    var yOffset = 9;
    alignments.forEach(function(item){
        var entity1Loc = entityToIdMap[item[1]];
        var entity2Loc = entityToIdMap[item[2]];
        var blockID = 'svg-text';
        var x1 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).x +
                document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[1])).x) / 2;
        var y1 = document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).y + yOffset;
        blockID = 'svg-hyp';
        var x2 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).x +
                document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[1])).x) / 2;
        var y2 = document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).y - yOffset -8;
        d3.select("svg").append("path").attr("d", positionLink(x1, y1, x2, y2)).
                                        attr("stroke", "red").
                                        attr("fill", "none").
                                        attr("class", "alignment").
                                        attr("opacity", 0.2).
                                        attr("stroke-width", "1");
    });

    highlightSpans(nerSL, "svg-text", sourceBaseY, "ner_source");
    highlightSpans(nerTL_projected, "svg-hyp", targetBaseY, "ner_projected");

    highlightSpans(framesSL, "svg-text", sourceBaseY, "frames_source");

    highlightSpans(framesTL_predicted, "svg-hyp", targetBaseY, "frames_predicted");

    highlightSpans(framesTL_projected, "svg-hyp", targetBaseY, "frames_projected");

    <!--// get x-y coordinates for edge start-ends-->
    arcsSL.forEach(function(item){
         console.log(item[1]);
         console.log(item[2]);
         console.log(' ');
        var entity1Loc = entityToIdMap[item[1]];
        var entity2Loc = entityToIdMap[item[2]];

        var blockID = 'svg-text';

        var x1 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).x +
                document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[1])).x) / 2;
        var y1 = document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).y - yOffset -8;

        var x2 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).x +
                document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[1])).x) / 2;
        var y2 = document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).y - yOffset -8;

        d3.select("svg").append("path").attr("d", positionArc(x1, y1, x2, y2)).
                                        attr("stroke", "gray").
                                        attr("fill", "none").
                                        attr("class", "dependency_source").
                                        attr("stroke-width", "1");

        xI = (x1 + x2) / 2;
        yI = y1 - ((x2 - x1) / 6);

        d3.select("svg").append("text")
                        .attr("x", xI)
                        .attr("y", yI)
                        .attr('text-anchor', 'middle')
                        .attr("class", "deprel_source")//easy to style with CSS
                        .text(item[3]);
    });

    arcsTL_predicted.forEach(function(item){
        var entity1Loc = entityToIdMap[item[1]];
        var entity2Loc = entityToIdMap[item[2]];

        var blockID = 'svg-hyp';

        var x1 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).x +
                document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[1])).x) / 2;
        var y1 = document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).y - yOffset -8;

        var x2 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).x +
                document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[1])).x) / 2;
        var y2 = document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).y - yOffset -8;

        d3.select("svg").append("path").attr("d", positionArc(x1, y1, x2, y2)).
                                                attr("stroke", "gray").
                                                attr("fill", "none").
                                                attr("class", "dependency_predicted").
                                                attr("stroke-width", "1");

        xI = (x1 + x2) / 2;
        yI = y1 - ((x2 - x1) / 6);

        d3.select("svg").append("text")
                        .attr("x", xI)
                        .attr("y", yI)
                        .attr('text-anchor', 'middle')
                        .attr("class", "deprel_predicted")//easy to style with CSS
                        .text(item[3]);
    });

     arcsTL_projected.forEach(function(item){
            var entity1Loc = entityToIdMap[item[1]];
            var entity2Loc = entityToIdMap[item[2]];

            var blockID = 'svg-hyp';

            var x1 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).x +
                    document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[1])).x) / 2;
            var y1 = document.getElementById(blockID).getStartPositionOfChar(Number(entity1Loc[0])).y - yOffset -8;

            var x2 = (document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).x +
                    document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[1])).x) / 2;
            var y2 = document.getElementById(blockID).getStartPositionOfChar(Number(entity2Loc[0])).y - yOffset -8;

            d3.select("svg").append("path").attr("d", positionArc(x1, y1, x2, y2)).
                                                    attr("stroke", "gray").
                                                    attr("fill", "none").
                                                    attr("class", "dependency_projected").
                                                    attr("stroke-width", "1");

            xI = (x1 + x2) / 2;
            yI = y1 - ((x2 - x1) / 6);

            d3.select("svg").append("text")
                            .attr("x", xI)
                            .attr("y", yI)
                            .attr('text-anchor', 'middle')
                            .attr("class", "deprel_projected")//easy to style with CSS
                            .text(item[3]);
        });

    var hyp = d3.select('svg').select("#svg-hyp").node();
    var hypLabel = d3.select('svg').select("#label-hyp").node();
    var text = d3.select('svg').select("#svg-text").node();
    var textLabel = d3.select('svg').select("#label-text").node();
    var parent = hyp.parentNode;
    d3.select('svg').selectAll("rect").each(function(d, i){
        parent.insertBefore(this, hyp);
        parent.insertBefore(this, hypLabel);
        parent.insertBefore(this, text);
        parent.insertBefore(this, textLabel);
    })

}

 // this function defines the curve of the edges
function positionLink(x1, y1, x2, y2) {
    return "M" + x1 + "," + y1
            + "S" + (x1+x2)/2 + "," + (y1+y2)/2
            + " " + x2 + "," + y2;
}

function positionArc(x1, y1, x2, y2) {
    xI = (x1 + x2) / 2;
    yI = y1 - ((x2 - x1) / 3);
    return "M" + x1 + "," + y1
            + "S" + xI + "," + yI
            + " " + x2 + "," + y2;
}

function toggle_visibility(id) {
    $(id).toggle();
}



function updateVisualization(){

    console.log('click');

    // visibility of POS
    var show_source_pos = $('#show_source_pos').is(':checked');
    if (show_source_pos)
      $('.pos_source').css("visibility", "visible");
    else
      $('.pos_source').css("visibility", "hidden");


    var show_predicted_pos = $('#show_predicted_pos').is(':checked');
    if (show_predicted_pos)
      $('.pos_predicted').css("visibility", "visible");
    else
      $('.pos_predicted').css("visibility", "hidden");


    var show_projected_pos = $('#show_projected_pos').is(':checked');
    if (show_projected_pos)
      $('.pos_projected').css("visibility", "visible");
    else
      $('.pos_projected').css("visibility", "hidden");


    // visibility of deprels
    var show_source_deps = $('#show_source_deps').is(':checked');
    if (show_source_deps){
          $('.dependency_source').css("visibility", "visible");
          $('.deprel_source').css("visibility", "visible");
    }
    else{
          $('.dependency_source').css("visibility", "hidden");
          $('.deprel_source').css("visibility", "hidden");
    }


    var show_predicted_deps = $('#show_predicted_deps').is(':checked');
    if (show_predicted_deps){
          $('.dependency_predicted').css("visibility", "visible");
          $('.deprel_predicted').css("visibility", "visible");
    }
    else{
          $('.dependency_predicted').css("visibility", "hidden");
          $('.deprel_predicted').css("visibility", "hidden");
    }

    var show_projected_deps = $('#show_projected_deps').is(':checked');
    if (show_projected_deps){
          $('.dependency_projected').css("visibility", "visible");
          $('.deprel_projected').css("visibility", "visible");
    }
    else{
          $('.dependency_projected').css("visibility", "hidden");
          $('.deprel_projected').css("visibility", "hidden");
    }

    // visibility of NER
    var show_source_ner = $('#show_source_ner').is(':checked');
    if (show_source_ner)
      toggle_span("ner_source", "visible");
    else
      toggle_span("ner_source", "hidden");


    var show_projected_ner = $('#show_projected_ner').is(':checked');
    if (show_projected_ner)
      toggle_span("ner_projected", "visible");
    else
      toggle_span("ner_projected", "hidden");

    // visibility of SRL
    var show_source_srl = $('#show_source_srl').is(':checked');
    if (show_source_srl)
      toggle_span("frames_source", "visible");
    else
      toggle_span("frames_source", "hidden");

    var show_predicted_srl = $('#show_predicted_srl').is(':checked');
    if (show_predicted_srl)
      toggle_span("frames_predicted", "visible");
    else
      toggle_span("frames_predicted", "hidden");

    var show_projected_srl = $('#show_projected_srl').is(':checked');
    if (show_projected_srl)
      toggle_span("frames_projected", "visible");
    else
      toggle_span("frames_projected", "hidden");

    console.log(show_projected_pos);
}

function toggle_span(label, visibility) {
    $('.span_' + label).css("visibility", visibility);
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};
