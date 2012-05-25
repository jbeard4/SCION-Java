var stitch  = require('stitch');
var fs      = require('fs');

var pkg = stitch.createPackage({
  paths: [__dirname + '/../../js-lib/SCION/lib/']
});

pkg.compile(function (err, source){
  fs.writeFile('build/SCION.js', source, function (err) {
    if (err) throw err;
    console.log('Compiled scion.js');
  });
});
