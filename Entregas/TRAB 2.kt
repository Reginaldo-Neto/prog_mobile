package com.example.listatarefas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listatarefas.ui.theme.ListaTarefasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListaTarefasTheme {
                ToDoApp()
            }
        }
    }
}

//Reginaldo Gregório de Souza Neto

@Composable
fun ToDoApp() {
    var text by remember { mutableStateOf("") } //Vou usar pra dar nome na tarefa
    val toDoTasks = remember { mutableStateListOf<Task>() } //Vou usar pra saber onde cada tarefa deve estar
    val doneTasks = remember { mutableStateListOf<Task>() } //Mesmo do de cima

    Column(modifier = Modifier.padding(16.dp)) {
        // Campo para adicionar novas tarefas
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Adicionar nova tarefa") },
            modifier = Modifier.fillMaxWidth() //Esse aqui é pra ocupar a linha toda
        )
        Button(
            onClick = {
                if (text.isNotBlank()) { //Verifiquei pra ver se a tarefa nao tava vazia
                    toDoTasks.add(Task(description = text)) //Sempre quando cria vai pra ToDo
                    text = ""//Limpei o campo pra digitar uma nova tarefa se quiser
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Adicionar")
        }
        // Lista To Do
        Text("To Do", style = MaterialTheme.typography.headlineMedium)//Aqui eu tive que pesquisar pra saber como coloca a letra grande
        TaskList(tasks = toDoTasks, onCheckTask = { task ->//Se clicar no checkbox muda de lista
            toDoTasks.remove(task)
            doneTasks.add(task.copy(isDone = true))
        }, onRemoveTask = { taskToRemove ->
            toDoTasks.remove(taskToRemove)
        })

        // Lista Done
        Text("Done", style = MaterialTheme.typography.headlineMedium)
        TaskList(tasks = doneTasks, onCheckTask = { task ->//Inverso do anterior
            doneTasks.remove(task)
            toDoTasks.add(task.copy(isDone = false))
        }, onRemoveTask = { taskToRemove ->
            doneTasks.remove(taskToRemove)
        })
    }
}

@Composable
fun TaskList(tasks: List<Task>, onCheckTask: (Task) -> Unit, onRemoveTask: (Task) -> Unit) {
    LazyColumn {
        items(items = tasks, key = { it.id }) { task ->
            TaskItem(task = task, onCheckTask = onCheckTask, onRemoveTask = onRemoveTask)
        }
    }
}

@Composable
fun TaskItem(task: Task, onCheckTask: (Task) -> Unit, onRemoveTask: (Task) -> Unit) {
    val openDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { _ -> onCheckTask(task) }
        )
        Text(
            text = task.description,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        IconButton(onClick = { openDialog.value = true }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }
    }

    if (openDialog.value) {
        ConfirmDeleteDialog(
            onDismiss = { openDialog.value = false },//Isso é praticamente um if else
            onConfirm = { //A depender doq o usuário clicar na função abaixo, ele "retorna"
                onRemoveTask(task) //pra uma dessas duas opções passadas por parametro aqui
                openDialog.value = false
            }
        )
    }
}

@Composable //Confirmação da exclusão
fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) { //Recebe as opcoes por parametro
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Confirmação") },
        text = { Text(text = "Tem certeza que deseja deletar esta tarefa?") },
        confirmButton = { //Faz o botão pra cada opção
            TextButton(onClick = { onConfirm() }) {
                Text("Deletar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}


data class Task( //Usei os atributos que o prof sugeriu na aula
    val id: Int = TaskIdGenerator.nextId,
    val description: String,
    var isDone: Boolean = false)

object TaskIdGenerator { //Tive que pesquisar como criar os ID únicos
    private var currentId = 0
    val nextId: Int
        get() = currentId++
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ListaTarefasTheme {
        ToDoApp()
    }
}
