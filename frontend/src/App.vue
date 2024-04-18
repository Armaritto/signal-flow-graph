<template>
  <div id="app">
    <h1> Signal Flow Graph</h1>
    <div class="tool-wrapper">
      <!-- <select v-model="newNodeType">
        <option v-for="(item, index) in nodeCategory" :key="index" :value="index">{{item}}</option>
      </select> -->
      <!-- <input type="text" v-model="newNodeLabel" placeholder="Input node label"> -->
      <button @click="addNode">ADD NODE</button>
      <button @click="getTransferFunction">GET TRANSFER FUNCTION</button>
    </div>
    
    <simple-flowchart :scene.sync="scene" 
      @nodeClick="nodeClick"
      @nodeDelete="nodeDelete"
      @linkBreak="linkBreak"
      @linkAdded="linkAdded"
      @canvasClick="canvasClick"
      :height="800"/>

    <TransferFunctionDisplay @close-display="showTransferFunctionDisplay = false" v-if="showTransferFunctionDisplay" :displayText="displayText"/>
  </div>
</template>

<script>
import SimpleFlowchart from './components/SimpleFlowchart.vue'
import TransferFunctionDisplay from './components/TransferFunctionDisplay.vue'

export default {
  name: 'app',
  components: {
    SimpleFlowchart,
    TransferFunctionDisplay
  },
  data() {
    return {
      displayText: 'This is a display text',
      showTransferFunctionDisplay: false,
      scene: {
        centerX: 1024,
        centerY: 140,
        scale: 1,
        nodes: [],
    
        links: [],
        
      },
      newNodeType: 0,
      newNodeLabel: '',
      nodeCategory:[
        'M',
        'Q'
      ],
    }
  },
  methods: {
    getTransferFunction() {
      var adjMatrix = []
      var nodes = this.scene.nodes
      var links = this.scene.links
      var n = nodes.length
      for (var i = 0; i < n; i++) {
        adjMatrix.push([])
        for (var j = 0; j < n; j++) {
          adjMatrix[i].push(0)
        }
      }
      for (var i = 0; i < links.length; i++) {
        adjMatrix[links[i].from-1][links[i].to-1] = links[i].weight
      }
      
    // console.log('adjMatrix:', adjMatrix)
    // adjMatrix = JSON.stringify(adjMatrix)
    // console.log('JSON format: \n', adjMatrix)

      console.log('adjMatrix:', adjMatrix)
      adjMatrix = JSON.stringify(adjMatrix)
      const url = "http://localhost:8080/programming_assignment/computeTransferFunction?"
          const params = {
            
          }
          const query = new URLSearchParams(params)
          const method = "POST"
          const body = adjMatrix
          
          fetch(url+query, {
            method: method,
            body: body,
            headers: {
              'Content-Type': 'application/json'
            },
    })
          .then(res => res.text())
          .then((data) => {this.displayText = data; this.showTransferFunctionDisplay = true ;console.log(data)})
    
  },
    canvasClick(e) {
      console.log('canvas Click, event:', e)
    },
    addNode() {
      let maxID = Math.max(0, ...this.scene.nodes.map((link) => {
        return link.id
      }))
      this.scene.nodes.push({
        id: maxID + 1,
        x: -400,
        y: 50,
        type: this.nodeCategory[this.newNodeType],
        label: this.newNodeLabel ? this.newNodeLabel: `test${maxID + 1}`,
      })
    },
    nodeClick(id) {
      console.log('node click', id);
    },
    nodeDelete(id) {
      console.log('node delete', id);
    },
    linkBreak(id) {
      console.log('link break', id);
    },
    linkAdded(link) {
      console.log('new link added:', link);
    }
  }
}
</script>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin: 0;
  overflow: hidden;
  height: 800px;
  .tool-wrapper {
    position: relative;
  }
}
</style>
