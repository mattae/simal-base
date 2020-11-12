function randomString(length) {
    var chars = '1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var randomString = '_';
    for (var i = 0; i < length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        randomString += chars.substring(rnum, rnum + 1);
    }
    return randomString;
}

function convert(content, moduleName, moduleToImport) {
    var ast = esprima.parseModule(content);
    var first = false;
    var global = 'global';
    var factory = 'factory';
    var exports = 'exports';
    var exportedCoreModuleRef = moduleName;
    var coreModuleRef = moduleName;
    var importedVariable = randomString(5);
    estraverse.traverse(ast, {
        enter: function (node) {
            if (node.type === 'CallExpression' && node.arguments && node.arguments.length === 3 &&
                node.arguments[1].value === '__esModule') {
                exports = node.arguments[0].name;
            }
        }
    });
    estraverse.traverse(ast, {
        enter: function (node) {
            if (node.type === 'AssignmentExpression' && node.left.object && node.left.object.name === exports
                && node.left.property.name === moduleName) {
                exportedCoreModuleRef = node.right.name;
            }
        }
    });
    estraverse.traverse(ast, {
        enter: function (node) {
            if (node.type === 'VariableDeclarator' && node.id.name === exportedCoreModuleRef) {
                coreModuleRef = node.init.callee.body.body[0].id.name;
            }
        }
    });
    estraverse.traverse(ast, {
        enter: function (node) {
            if (node.type === 'FunctionExpression' && node.params.length === 2) {
                if (!first) {
                    first = true;
                    global = node.params[0].name;
                    factory = node.params[1].name;
                }
                first = false;
                return node;
            }
            if (node.type === 'CallExpression' && node.arguments && node.arguments[0] &&
                node.arguments[0].type === 'ThisExpression' && node.arguments[1] &&
                node.arguments[1].type === 'FunctionExpression' && node.arguments[1].body &&
                node.arguments[1].body.type === 'BlockStatement' && node.arguments[1].body.body &&
                node.arguments[1].body.body[0].directive) {
                node.arguments[1].params.push({
                    type: 'Identifier',
                    name: importedVariable
                });
                return node;
            }
            if (node.type === 'ConditionalExpression' && node.consequent.type === 'CallExpression' &&
                node.consequent.callee.type === 'Identifier' && node.consequent.callee.name === factory) {
                node.consequent.arguments.push({
                    type: 'CallExpression',
                    callee: {
                        type: 'Identifier',
                        name: 'require'
                    },
                    arguments: [{
                        type: 'Literal',
                        value: moduleToImport
                    }]
                });
                return node;
            }
            if (node.type === 'ArrayExpression' && node.elements && node.elements[0]
                && node.elements[0].type === 'Literal' && node.elements[0].value === 'exports') {
                node.elements.push({
                    type: 'Literal',
                    value: moduleToImport
                });
                return node;
            }
            if (node.type === 'CallExpression' && node.callee.type === 'Identifier' && node.callee.name === factory
                && node.arguments && node.arguments.length > 1) {
                if (!first) {
                    first = true;
                } else {
                    var globalName = node.arguments[1].object.object.name || global;

                    node.arguments.push({
                        type: 'MemberExpression',
                        object: {
                            type: 'Identifier',
                            name: globalName
                        },
                        property: {
                            type: 'Identifier',
                            name: importedVariable
                        },
                        computed: false
                    });
                }
                return node;
            }

            /*//Import section 7
            if (node.type === 'AssignmentExpression' && node.left.property && node.left.property.name === 'decorators'
                && node.left.object.name === coreModuleRef
            ) {
                var properties = node.right.elements[0].properties[1].value.elements[0].properties;
                var index = 0;
                for (; index < properties.length; index++) {
                    if (properties[index].key.name === 'imports') {
                        break;
                    }
                }
                print('To add imports here');
                node.right.elements[0].properties[1].value.elements[0].properties[index].value.elements.push({
                    type: 'Identifier',
                    name: importedVariable + '.' + moduleToImport
                });
                return node;
            }*/

            //Import section 8
            /*if (node.type === 'AssignmentExpression' && node.left.type === 'Identifier' && node.left.name === coreModuleRef
                && node.right.type === 'CallExpression' && node.right.callee.name === '__decorate'
            ) {
                var properties = node.right.arguments[0].elements[0].arguments[0].properties;
                var index = 0;
                for (; index < properties.length; index++) {
                    if (properties[index].key.name === 'imports') {
                        break;
                    }
                }
                node.right.arguments[0].elements[0].arguments[0].properties[index].value.elements.push({
                    type: 'Identifier',
                    name: importedVariable + '.' + moduleToImport
                });
                return node;
            }*/

            //Import section 8 minified
            if (node.type === 'Property' && node.key && node.key.type === 'Identifier' && node.key.name === 'imports'
                && node.value && node.value.type === 'ArrayExpression'
            ) {

                node.value.elements.push({
                    type: 'Identifier',
                    name: importedVariable + '.' + moduleToImport
                });
                return node;
            }
        }
    });
    return escodegen.generate(ast);
}
