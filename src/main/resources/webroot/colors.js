$(document).ready(function(){
  const curRGB = []

  const startColor = Math.floor(Math.random() * 2) == 0 ? 'rgb(0,0,0)' : 'rgb(255,255,255)'
  $('#testColor').css('background-color',startColor)
  getCurColor()
  getColorInference(curRGB, setColorClassification, alertFailure)

  function setColorClassification(data, textStatus, jqxhr) {
    console.log(JSON.stringify(jqxhr, null, '  '))
    $("input[name='color'][value='" + data.color + "']").prop('checked', true);
    $('#inference').text(data.timeMs)
  }

  function alertFailure(jqxhr, textStatus, error) {
    alert("Error retry\r\n" + JSON.stringify(jqxhr, null, '  '))
  }

  function getCurColor() {
    const rgb = $('#testColor').css('background-color')
    const colors = rgb.slice(4,-1).split(',')
    curRGB.length = 0
    for(let i=0; i<3; i++) {
      curRGB.push(parseInt(colors[i], 10))
    }
  }

  function getColorInference(curRGB, successCB, failureCB) {
    const data = {
      rgb: curRGB
    }
    console.log(data)

    postData(Config.getColorInferenceUrl(), data, successCB, failureCB)
  }

  $('#testColorSubmit').click(function() {
    const r = Math.round(Math.random() * 255)
    const g = Math.round(Math.random() * 255)
    const b = Math.round(Math.random() * 255)
    const color = 'rgb(' + r + ',' + g + ',' + b + ')'
    $('#testColor').css('background-color',color)
  })

  $('#testColorSubmit').click(function() {
    getCurColor()
    let colorClass = $('input[name=color]:checked').val()
    console.log(colorClass)

    const data = {
      rgb: curRGB,
      colorClass
    }
    postData(Config.getColorTrainValidateUrl(), data, function(data, textStatus, jqxhr) {
      console.log(data)
      $("input[name='color'][value='" + data.color + "']").prop('checked', true);
      $('#inference').text(data.timeMs)
    },
    function(jqxhr, textStatus, error) {
      console.log("Error, retry\r\n" + JSON.stringify(jqxhr, null, '  '))
    })
  })

  function postData(url, data, successCB, failureCB) {
    //console.log(data)
    //var jqxhr = $.post("http://localhost:2525/", data)

    //$.post("http://localhost:8080/test", data, successCB, 'json')
    $.ajax({
    	url,
    	type: 'POST',
    	data: JSON.stringify(data),
    	contentType: 'application/json; charset=utf-8',
    	dataType: 'json'
    })
    .done(successCB)
    .fail(failureCB);
  }
})
