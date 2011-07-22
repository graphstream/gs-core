Author "GraphStream"
Graph [
    Node [
        Id 1
        Label "AYZE34"
        Graphics [
            x 0
            y 0
            w 10
            h 10
            fill "#fbf6fb"
            outline "#cccccc"
            outline_width 1
            type "ellipse"
        ]
    ]
    Node [
        Id 2
        Label "UYET29"
        Graphics [
            x 0.5
            y 1
            w 10
            h 10
            fill "#daf0da"
            outline "#cccccc"
            outline_width 1
            type "ellipse"
        ]
    ]
    Node [
        Id 3
        Label "DSJHI45"
        Graphics [
            x 1
            y 0
            w 10
            h 10
            fill "#f7fcf7"
            outline "#cccccc"
            outline_width 1
            type "ellipse"
        ]
    ]
    Directed true
    Edge [
        source 1
        target 2
        Graphics [
            type "line"
            fill "#9999ff"
            width 1.0
        ]
        Label "U"
    ]

    Edge [
        source 2
        target 3
        Graphics [
            type "line"
            fill "#9999ff"
            width 1.0
        ]
        Label "Y"
    ]
    Edge [
        source 3
        target 1
        Graphics [
            type "line"
            fill "#9999ff"
            width 1.0
        ]
        Label "O"
    ]
]
Version "1.0"