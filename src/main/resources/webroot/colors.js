$(document).ready(function(){
  const train = true
  const eval = false

  const rgbScale = 1/255
  const curRGB = []

  const startColorIdx = Math.round(Math.random())
  const startColorLabel = startColorIdx == 0 ? 'black' : 'white'
  const startColor = startColorIdx == 0 ? 'rgb(0,0,0)' : 'rgb(255,255,255)'
  $('#testColor').css('background-color',startColor)
  $("input[name='color'][value='" + startColorLabel + "']").prop('checked', true);
  getCurColor()
  postObservationAndInferNextColor({rgb: curRGB}, train)

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

  function getColorInference(color, successCB, failureCB) {
    const data = {
      color,
      rgb: curRGB
    }
    console.log(data)

    postData(Config.getColorInferenceUrl(), data, successCB, failureCB)
  }

  $('#train').click(function() {
    trainEval(train)
  })

  $('#eval').click(function() {
    trainEval(eval)
  })

  function trainEval(train) {
    const r = Math.round(Math.random() * 255)
    const g = Math.round(Math.random() * 255)
    const b = Math.round(Math.random() * 255)
    const color = 'rgb(' + r + ',' + g + ',' + b + ')'
    //changing color randomly before posting observation
    //gen new color and send with post observation for inference, get back color prediction
    //set new color and prediction in radio button
    postObservationAndInferNextColor({rgb: [r, g, b]}, train)
  }

  function postObservationAndInferNextColor(nextColor, train) {
    getCurColor()
    let color = $('input[name=color]:checked').val()
    console.log(color)

    const data = {
      train,
      color,
      rgb: curRGB,
      nextColor
    }

    $('#rInt').text(nextColor.rgb[0])
    $('#gInt').text(nextColor.rgb[1])
    $('#bInt').text(nextColor.rgb[2])
    $('#rScaled').text((nextColor.rgb[0]*rgbScale).toFixed(2))
    $('#gScaled').text((nextColor.rgb[1]*rgbScale).toFixed(2))
    $('#bScaled').text((nextColor.rgb[2]*rgbScale).toFixed(2))

    postData(Config.getColorTrainValidateUrl(), data, function(data, textStatus, jqxhr) {
      console.log(data)
      $("input[name='color'][value='" + data.color + "']").prop('checked', true);
      const rgb = 'rgb(' + nextColor.rgb.join(',') + ')'
      $('#testColor').css('background-color',rgb)
      $('#inference').text(data.timeMs.toFixed(2))
      $('#stats').text(data.stats)

      for (i=0; i<11; i++) {
        $('#out' + i + 'val').text(data.colorProbabilities[i].toFixed(3))
        $('#out' + i + 'line').width(Math.round(data.colorProbabilities[i]*50))
      }
    },
    function(jqxhr, textStatus, error) {
      console.log("Error, retry\r\n" + JSON.stringify(jqxhr, null, '  '))
    })
  }

  $('#reset').click(function() {
    postData(Config.getModelAdminUrl(), {resetModel: true})
  })

  $('#save').click(function() {
    postData(Config.getModelAdminUrl(), {saveModel: true, modelFilename: $('#filename').val()})
  })

  $('#load').click(function() {
    postData(Config.getModelAdminUrl(), {loadModel: true, modelFilename: $('#filename').val()})
  })

  function postData(url, data, successCB, failureCB) {
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
