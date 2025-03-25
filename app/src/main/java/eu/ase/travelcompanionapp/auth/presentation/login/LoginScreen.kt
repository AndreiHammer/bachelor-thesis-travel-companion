package eu.ase.travelcompanionapp.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.ase.travelcompanionapp.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val loginState = viewModel.loginState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = stringResource(R.string.auth_image),
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            placeholder = { Text(stringResource(R.string.email)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = stringResource(R.string.email)
                )
            }
        )

        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
                .border(
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp))


        Button(
            onClick = { viewModel.onLoginClick(context) },
            enabled = loginState.value != LoginState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = stringResource(R.string.login),
                fontSize = 16.sp,
                modifier = Modifier.padding(0.dp, 6.dp)
            )
        }

        when (val state = loginState.value) {
            is LoginState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is LoginState.Error -> {
                Toast.makeText(context, state.message.asString(context), Toast.LENGTH_SHORT).show()
                LaunchedEffect(Unit) {
                    viewModel.resetState()
                }
            }
            is LoginState.Success -> {
                Toast.makeText(context,
                    stringResource(R.string.login_successful), Toast.LENGTH_SHORT).show()
                LaunchedEffect(Unit) {
                    onLoginClick(email.value, password.value)
                }

            }
            else -> Unit
        }
    }
}