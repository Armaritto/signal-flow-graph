<template>
    <div>
        <div class="modal">
            <div class="modal-content">
                
                <button @click="closeDisplay"> OK </button>
                
                <p>{{ displayText }}</p>

            </div>
        </div>
    </div>
</template>

<script>
//import { ref } from 'vue';

export default {
    props:['displayText'],
    methods: {
        closeDisplay() {
            this.$emit('close-display');
        }
    },
    data() {
      return {
        
      }
    },
   

}
</script>

<style scoped>
.modal {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    position: fixed;
    z-index: 1;
    left: 0;
    top: 0;
    width: 30%;
    height: 100%;
    overflow: auto;
    resize: both;
    background-color: rgba(0,0,0,0.4);
}

.modal-content {
    background-color: #fefefe;
    margin: 0;
    padding: 20px;
    border: 1px solid #888;
    width: 100%;
    height: 100%;
    box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2), 0 6px 20px 0 rgba(0,0,0,0.19);
    border-radius: 10px; /* Add a border radius to create a frame-like appearance */
}

.modal-content button {
    background-color: rgb(255, 136, 85); /* Change the background color */
    border: none; /* Remove the border */
    color: white; /* Change the text color */
    padding: 15px 32px; /* Add some padding */
    text-align: center; /* Center the text */
    text-decoration: none; /* Remove the underline */
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
    border-radius: 4px; /* Add a border radius */
}
.modal-content p {
    font-family: Arial, sans-serif;
    font-size: 22px;
    color: #333;
    line-height: 1.6;
    margin-bottom: 20px;
    text-align: justify;
    white-space: pre-line;
    
}

.close {
    color: white;
    float: right;
    font-size: 28px;
    font-weight: bold;
}

.close:hover,
.close:focus {
    color: #000;
    text-decoration: none;
    cursor: pointer;
}
.close-button {
    background: none;
    border: none;
    cursor: pointer;
}
@keyframes animateleft {
    from {left: -300px; opacity: 0}
    to {left: 0; opacity: 1}
}
</style>

