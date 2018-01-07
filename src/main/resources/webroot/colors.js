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
  setSliderColor(curRGB)
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
      nextColor,
    }

    $('#rInt').text(nextColor.rgb[0])
    $('#gInt').text(nextColor.rgb[1])
    $('#bInt').text(nextColor.rgb[2])
    $('#rScaled').text((nextColor.rgb[0]*rgbScale).toFixed(2))
    $('#gScaled').text((nextColor.rgb[1]*rgbScale).toFixed(2))
    $('#bScaled').text((nextColor.rgb[2]*rgbScale).toFixed(2))

    postData(Config.getColorTrainValidateUrl(), data,
      function(data, textStatus, jqxhr) {
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

        getCurColor()
        setSliderColor(curRGB)
      },
      function(jqxhr, textStatus, error) {
        alert("Error, retry\r\n" + JSON.stringify(jqxhr, null, '  '))
      })
  }

  $('#reset').click(function() {
    postData(Config.getModelAdminUrl(), {resetModel: true},
      function(data, textStatus, jqxhr) {
        alert("Successfully reset model and cleared training and eval DataSets")
      },
      function(jqxhr, textStatus, error) {
        alert("Error resetting model\r\n" +  JSON.stringify(jqxhr, null, '  '))
      })
  })

  $('#save').click(function() {
    postData(Config.getModelAdminUrl(), {saveModel: true, modelFilename: $('#filename').val()},
      function(data, textStatus, jqxhr) {
        alert("Successfully saved model " + $('#filename').val())
      },
      function(jqxhr, textStatus, error) {
        alert("Error saving model\r\n" +  JSON.stringify(jqxhr, null, '  '))
      })
  })

  $('#load').click(function() {
    postData(Config.getModelAdminUrl(), {loadModel: true, modelFilename: $('#filename').val()},
      function(data, textStatus, jqxhr) {
        alert("Successfully loaded model " + $('#filename').val())
      }, function(jqxhr, textStatus, error) {
        alert("Error loading model\r\n" +  JSON.stringify(jqxhr, null, '  '))
      })
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

  const sliders = {
    changed: false,
    rDown: false,
    gDown: false,
    bDown: false,
    curSlider: 'none',
  }

  $('.sliderHolder').mousedown(function(event) {
    event.preventDefault()
    const slider = this.id.substring(0,1)
    sliders.curSlider = slider
    sliders[slider + 'Down'] = true
    const color = 255-event.offsetY
    $('#' + slider + 'Slider').height(color)
    setColor(slider, color)
  })

  $('.sliderHolder').mousemove(function(event) {
    event.preventDefault()
    const slider = this.id.substring(0,1)
    if (event.buttons == 1 && sliders.curSlider === slider) {
      sliders[slider + 'Down'] = true
    } else {
      sliders[slider + 'Down'] = false
      return
    }
    const color = Math.min(255 - event.offsetY, 255)
    $('#' + slider + 'Slider').height(color)
    setColor(slider, color)
    sliders.changed = true
  })

  $('body').mouseup(function(event) {
    event.preventDefault()
    const slider = this.id.substring(0,1)
    sliders[slider + 'Down'] = false
    if (sliders.changed) {
      sliders.changed = false
      const nextColor = { rgb: curRGB } //not changing
      const train = false //not training
      postObservationAndInferNextColor(nextColor, train)
    }
  })

  $('.sliderHolder').mouseout(function(event) {
    event.preventDefault()
    const slider = this.id.substring(0,1)
    sliders[slider + 'Down'] = false
  })

  function setSliderColor(curRGB) {
    $('#rSlider').height(curRGB[0])
    $('#gSlider').height(curRGB[1])
    $('#bSlider').height(curRGB[2])
  }

  function setColor(slider, color) {
    switch(slider) {
      case 'r':
        curRGB[0] = color
        $('#' + slider + 'Int').text(curRGB[0])
        $('#' + slider + 'Scaled').text((curRGB[0]*rgbScale).toFixed(2))
        break
      case 'g':
        curRGB[1] = color
        $('#' + slider + 'Int').text(curRGB[1])
        $('#' + slider + 'Scaled').text((curRGB[1]*rgbScale).toFixed(2))
        break
      case 'b':
        curRGB[2] = color
        $('#' + slider + 'Int').text(curRGB[2])
        $('#' + slider + 'Scaled').text((curRGB[2]*rgbScale).toFixed(2))
        break
    }
    const newColor = 'rgb(' + curRGB[0] + ',' + curRGB[1] + ',' + curRGB[2] + ')'
    $('#testColor').css('background-color', newColor)
  }
})
