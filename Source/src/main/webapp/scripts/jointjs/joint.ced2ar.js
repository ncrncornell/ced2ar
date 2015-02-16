joint.ced2ar = {};
joint.ced2ar.Pentagon = joint.dia.Element.extend({

    markup: '<g class="rotatable"><g class="scalable"><polygon class="outer"/><polygon class="inner"/></g><text/></g>',
    
    defaults: joint.util.deepSupplement({

        type: 'erd.Pentagon',
        size: { width: 20, height: 20 },
        attrs: {
            '.outer': {
                fill: '#fff', stroke: '#d03d00', 'stroke-width': 2,
                points: '20,0 1,14 8,36 32,36 39,14'
            },
            '.inner': {
                fill: '#fff', stroke: '#000', 'stroke-width': 2,
                points: '20,0 1,14 8,36 32,36 39,14',
                display:'none'
            },
            text: {
                ref: '.', 'ref-x': 0.5, 'ref-y': 0.5,
                'x-alignment': 'middle', 'y-alignment': 'middle'
            }
        }
    }, joint.dia.Element.prototype.defaults)
});