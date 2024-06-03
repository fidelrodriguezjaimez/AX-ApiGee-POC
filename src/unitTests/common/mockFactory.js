const sinon = require("sinon");

let contextGetVariableMethod;
let contextSetVariableMethod;

beforeEach(function () {
  global.context = {
    getVariable: function (s) {
      // Fix solucion
    },
    setVariable: function (s, a) {
      // Fix solicion
    },
  };
  contextGetVariableMethod = sinon.stub(global.context, "getVariable");
  contextSetVariableMethod = sinon.spy(global.context, "setVariable");
});

afterEach(function () {
  contextGetVariableMethod.restore();
  contextSetVariableMethod.restore();
});

exports.getMock = function () {
  return {
    contextGetVariableMethod: contextGetVariableMethod,
    contextSetVariableMethod: contextSetVariableMethod,
  };
};