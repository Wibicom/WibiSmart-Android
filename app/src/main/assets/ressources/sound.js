var elem = $('.soundbar');
var count = elem.length;
var intensity = 1;

var loop = function(){ 
    elem.each(function(){
      var $this = $(this);
      var height = intensity*((Math.random() * 30) + 1);
      $this.css({
        'background': 'rgba(0, 0, 0,'+(.75-($this.index()/count)/2)+')',
        'bottom': height,
        'height': height
      });
    });
}

function updateSound(val) {
    document.getElementById('soundvalue').innerHTML = val + " dB";
    intensity = val/40;
    loop();
}
    
loop();