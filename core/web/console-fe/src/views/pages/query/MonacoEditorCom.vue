<template>
  <div id="container"  class="monacoEditorBox"></div> <!--宽高自行设定 -->
</template>

<script lang="ts">
import * as monaco from 'monaco-editor'
import {defineComponent, ref, toRaw} from "vue";
export default{
    props:{
      code: String
    },
    data(){
        return {
            editor:null,//文本编辑器
        }
    },
    mounted(){
      this.initEditor();
    },
    watch:{
      code(oldVal,newVal){
        // this.editor.
        console.log('监听code', newVal)
      }
    },
    methods:{
        initEditor(){
            // 初始化编辑器，确保dom已经渲染
            this.editor = monaco.editor.create(document.getElementById('container'), {
                value:'',//编辑器初始显示文字
                language:'sql',//语言支持自行查阅demo
                automaticLayout: true,//自动布局
                theme:'vs-dark' //官方自带三种主题vs, hc-black, or vs-dark
            });
            const editor = this.editor
            this.editor.onDidChangeModelContent((value)=>{
              const val = toRaw(editor).getValue()
              this.$emit('change',val)
            })
        },
        getValue(){
            return toRaw(this.editor).getValue(); //获取编辑器中的文本
        }
    }
}
</script>

<style scoped>

.monacoEditorBox{
  height: 300px;
}
</style>
